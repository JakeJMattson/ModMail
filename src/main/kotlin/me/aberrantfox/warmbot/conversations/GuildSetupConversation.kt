package me.aberrantfox.warmbot.conversations

import me.aberrantfox.warmbot.dsl.Convo
import me.aberrantfox.warmbot.dsl.Step
import me.aberrantfox.warmbot.dsl.conversation

@Convo
var guildSetupConversation = conversation {
    name = "guild-setup"
    description = "Conversation that takes place with a user whenever the bot joins a new guild."

    steps {
        step {
            prompt =
                    "I'm here to help you more effectively manage your report process."
            expectedResponseType = Step.ResponseType.Channel
        }
        step {
            prompt = "This is a second message that's expecting a guild id."
            expectedResponseType = Step.ResponseType.Guild
        }
    }

    onComplete {
        it.respond("Guild Successfully Configured.")
        return@onComplete
    }
}