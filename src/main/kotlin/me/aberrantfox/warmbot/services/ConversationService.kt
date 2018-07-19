package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.extensions.stdlib.trimToID
import me.aberrantfox.kjdautils.internal.command.tryRetrieveSnowflake
import me.aberrantfox.warmbot.dsl.Conversation
import me.aberrantfox.warmbot.dsl.ConversationStateContainer
import me.aberrantfox.warmbot.dsl.Convo
import me.aberrantfox.warmbot.dsl.Step
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import org.reflections.Reflections
import org.reflections.scanners.FieldAnnotationsScanner

sealed class ResponseResult {
    data class Error(val errorMessage: String) : ResponseResult()
}

class ConversationService(val jda: JDA, val configuration: Configuration, val reportService: ReportService) {

    private var availableConversations = mutableListOf<Conversation>()
    private val activeConversations = mutableListOf<ConversationStateContainer>()

    fun hasConversation(userId: String) = activeConversations.any { it.userId == userId }

    private fun getConversationState(
            userId: String): ConversationStateContainer = activeConversations.first { cs -> cs.userId == userId }

    private fun getCurrentStep(
            conversationState: ConversationStateContainer): Step = conversationState.conversation.steps[conversationState.currentStep]

    fun createConversation(userId: String, guildId: String, conversationName: String) {

        if (hasConversation(userId) || jda.getUserById(userId).isBot)
            return

        val conversation = availableConversations.first { c -> c.name == conversationName }
        activeConversations.add(ConversationStateContainer(userId, guildId, mutableListOf(),
                conversation, 0, jda, configuration))

        if (reportService.hasReportChannel(userId)) {
            reportService.sendReportClosedEmbed(reportService.getReportByUserId(userId))
            (jda.getTextChannelById(reportService.getReportByUserId(userId).channelId)).delete().queue()
        }

        sendToUser(userId, getCurrentStep(getConversationState(userId)).prompt)
    }

    fun handleResponse(userId: String, event: PrivateMessageReceivedEvent) {
        val conversationState = getConversationState(userId)
        val currentStep = getCurrentStep(conversationState)
        val totalSteps = conversationState.conversation.steps.size
        val response = parseResponse(event.message.contentStripped.trim(), getCurrentStep(conversationState))

        if (response is ResponseResult.Error) {
            sendToUser(userId, currentStep.prompt)
        } else {
            conversationState.responses.add(response)
            if (conversationState.currentStep < (totalSteps - 1)) {
                conversationState.currentStep++
                sendToUser(conversationState.userId, getCurrentStep(conversationState).prompt)
            } else {
                conversationState.conversation.onComplete.invoke(conversationState)
                activeConversations.remove(conversationState)
            }
        }
    }

    private fun parseResponse(message: String, step: Step): Any {

        when (message.isNotEmpty()) {
            step.expectedResponseType == Step.ResponseType.Channel -> {
                return tryRetrieveSnowflake(jda) { it.getTextChannelById(message.trimToID()) }
                        ?: return ResponseResult.Error("Couldn't retrieve text channel with parameter :: $message")
            }
            step.expectedResponseType == Step.ResponseType.Guild -> {
                return tryRetrieveSnowflake(jda) { it.getGuildById(message.trimToID()) }
                        ?: return ResponseResult.Error("Couldn't retrieve guild with parameter :: $message")
            }
            step.expectedResponseType == Step.ResponseType.User -> {
                return tryRetrieveSnowflake(jda) { it.getUserById(message.trimToID()) }
                        ?: return ResponseResult.Error("Couldn't retrieve user with parameter :: $message")
            }
            step.expectedResponseType == Step.ResponseType.Category -> {
                return tryRetrieveSnowflake(jda) { it.getCategoryById(message.trimToID()) }
                        ?: return ResponseResult.Error("Couldn't retrieve category with parameter :: $message")
            }
            step.expectedResponseType == Step.ResponseType.Role -> {
                return tryRetrieveSnowflake(jda) { it.getRoleById(it.getRolesByName(message, true).first().id) }
                        ?: return ResponseResult.Error("Couldn't retrieve role with name :: $message")
            }
            else -> return message
        }
    }

    private fun sendToUser(userId: String, message: Any) {
        if (message is MessageEmbed)
            jda.getUserById(userId).openPrivateChannel().queue {
                it.sendMessage(message).queue()
            }
        else
            jda.getUserById(userId).openPrivateChannel().queue {
                it.sendMessage(message as String).queue()
            }
    }

    fun registerConversations(path: String) =
        Reflections(path, FieldAnnotationsScanner()).getFieldsAnnotatedWith(Convo::class.java).forEach {
            it.trySetAccessible()
            availableConversations.add(it.get(it) as Conversation)
        }
}


