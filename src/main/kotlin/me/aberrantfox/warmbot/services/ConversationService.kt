package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.api.dsl.CommandsContainer
import me.aberrantfox.kjdautils.api.dsl.KJDAConfiguration
import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import me.aberrantfox.kjdautils.internal.command.cleanCommandMessage
import me.aberrantfox.warmbot.dsl.Conversation
import me.aberrantfox.warmbot.dsl.ConversationStateContainer
import me.aberrantfox.warmbot.dsl.Convo
import me.aberrantfox.warmbot.dsl.Step
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import org.reflections.Reflections
import org.reflections.scanners.FieldAnnotationsScanner


class ConversationService(val jda: JDA, val configuration: Configuration, private val reportService: ReportService,
                          private val config: KJDAConfiguration) {

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
        val response = parseResponse(event.message, getCurrentStep(conversationState))

        if (response is ArgumentResult.Error) {
            sendToUser(userId, response.error)
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

    private fun parseResponse(message: Message, step: Step): Any {
        val commandStruct = cleanCommandMessage(message.contentRaw, config)
        val commandEvent = CommandEvent(commandStruct, message, commandStruct.commandArgs, CommandsContainer())
        val result = step.expect.convert(message.contentStripped, commandEvent.commandStruct.commandArgs, commandEvent)

        return when (result) {
            is ArgumentResult.Single -> {
                result.result
            }
            is ArgumentResult.Multiple -> {
                result.result
            }
            is ArgumentResult.Error -> {
                result.error
            }
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


