package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.extensions.stdlib.trimToID
import me.aberrantfox.kjdautils.internal.command.tryRetrieveSnowflake
import me.aberrantfox.warmbot.dsl.Conversation
import me.aberrantfox.warmbot.dsl.Step
import me.aberrantfox.warmbot.dsl.conversation
import me.aberrantfox.warmbot.extensions.fullContent
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent

data class ConversationStateContainer(val userId: String, val guildId: String, var responses: MutableList<Any>,
                                      val conversation: Conversation, var currentStep: Int,
                                      val jda: JDA) {

    fun respond(message: String) {
        print("Hello there!")
        jda.getUserById(userId).sendPrivateMessage(message)
    }
}

sealed class ResponseResult {
    data class Argument(val value: Any) : ResponseResult()
    data class Error(val errorMessage: String) : ResponseResult()
}

class ConversationService(val jda: JDA, val configuration: Configuration) {

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

        availableConversations.add(setupConversation)
        val conversation = availableConversations.first { c -> c.name == conversationName }
        activeConversations.add(ConversationStateContainer(userId, guildId, mutableListOf(),
                conversation, 0, jda))

        jda.getUserById(userId).sendPrivateMessage(getCurrentStep(getConversationState(userId)).message)

    }

    fun handleResponse(userId: String, event: PrivateMessageReceivedEvent) {
        val conversationState = getConversationState(userId)
        val currentStep = getCurrentStep(conversationState)
        val totalSteps = conversationState.conversation.steps.size
        val result = parseResponse(event.message.fullContent().trim(), getCurrentStep(conversationState))

        if (result is ResponseResult.Error) {
            jda.getUserById(userId)
                    .sendPrivateMessage(currentStep.message)
        } else if (result is ResponseResult.Argument) {
            conversationState.responses.add(result)
            if (conversationState.currentStep < (totalSteps - 1)) {
                conversationState.currentStep++
                jda.getUserById(conversationState.userId).sendPrivateMessage(getCurrentStep(conversationState).message)
            } else {
                conversationState.conversation.onComplete.invoke(conversationState)
                activeConversations.remove(conversationState)
            }
        }
    }

    private fun parseResponse(message: String, step: Step): ResponseResult {
        if (step.responseType == Step.ResponseType.Channel) {
            val retrieved = tryRetrieveSnowflake(jda) { it.getTextChannelById(message.trimToID()) }
            return if (retrieved != null) {
                ResponseResult.Argument(retrieved)
            } else {
                ResponseResult.Error("Couldn't retrieve text channel with parameter :: $message")
            }
        } else if (step.responseType == Step.ResponseType.Guild) {
            val retrieved = tryRetrieveSnowflake(jda) { it.getGuildById(message.trimToID()) }
            return if (retrieved != null) {
                ResponseResult.Argument(retrieved)
            } else {
                ResponseResult.Error("Couldn't retrieve a guild with parameter :: $message")
            }
        } else if (step.responseType == Step.ResponseType.User) {
            val retrieved = tryRetrieveSnowflake(jda) { it.getUserById(message.trimToID()) }
            return if (retrieved != null) {
                ResponseResult.Argument(retrieved)
            } else {
                ResponseResult.Error("Couldn't retrieve a user with parameter :: $message")
            }
        } else
            return ResponseResult.Argument(message)
    }
}

private var setupConversation = conversation {

    name = "setup"
    description = "Conversation that takes place with a user whenever the bot joins a new guild."

    steps {
        step {
            message = "I'm Warmbot, let's get setup! Please give me a channel id."
            expectedResponseType = Step.ResponseType.Channel
        }
        step {
            message = "This is a second message that's expecting a guild id."
            expectedResponseType = Step.ResponseType.Guild
        }
    }

    onComplete {
       System.out.println("Hello");
        return@onComplete
    }
}


