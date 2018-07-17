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

data class ConversationStateContainer(val userId: String, val guildId: String, var responseMap: MutableMap<Int, Any>,
                                      val conversation: Conversation, var currentStep: Int,
                                      var complete: Boolean)

sealed class ResponseResult {
    data class Argument(val value: Any) : ResponseResult()
    data class Error(val errorMessage: String) : ResponseResult()
}

class ConversationService(val jda: JDA, val configuration: Configuration) {

    private var availableConversations = mutableListOf<Conversation>()
    private val activeConversations = mutableMapOf<String, ConversationStateContainer>()



    fun hasConversation(userId: String) = activeConversations.entries.any { it.key == userId }

    fun createConversation(userId: String, guildId: String, conversationName: String) {
        availableConversations.add(setupConversation)
        val conversation = availableConversations.first { c -> c.name == conversationName }
        activeConversations[userId] = ConversationStateContainer(userId, guildId, mutableMapOf<Int, Any>(),
                conversation, 0, false)

        sendAppropriatePrompt(jda, activeConversations[userId], false)
    }

    fun handleResponse(userId: String, event: PrivateMessageReceivedEvent) {
        val result = getResult(event.message.fullContent().trim(), getCurrentStep(activeConversations[userId]))

        if (result is ResponseResult.Error) {
            sendAppropriatePrompt(jda, activeConversations[userId], false)
        } else {
            sendAppropriatePrompt(jda, activeConversations[userId], true)
            activeConversations[userId]!!.responseMap[activeConversations[userId]!!.currentStep] = result
        }
    }

    private fun sendAppropriatePrompt(jda: JDA, conversationState: ConversationStateContainer?,
                                      lastArgumentValid: Boolean) {
        if (lastArgumentValid) {
            conversationState!!.currentStep++
            jda.getUserById(conversationState!!.userId)
                    .sendPrivateMessage(getCurrentStep(conversationState).message)
        } else {
            jda.getUserById(conversationState!!.userId).sendPrivateMessage(getCurrentStep(conversationState).message)
        }
    }

    private fun getCurrentStep(
            conversationState: ConversationStateContainer?): Step = conversationState!!.conversation.steps[conversationState.currentStep]

    private fun incrementStep(userId: String) {

    }

    private fun getResult(message: String, step: Step): ResponseResult {
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
            responseType = Step.ResponseType.Channel
        }
        step {
            message = "This is a second message that's expecting a guild id."
            responseType = Step.ResponseType.Guild
        }
    }
}


