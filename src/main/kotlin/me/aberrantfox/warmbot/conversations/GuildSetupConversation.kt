package me.aberrantfox.warmbot.conversations

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*
import java.awt.Color

@Convo
fun guildSetupConversation(config: Configuration, persistenceService: PersistenceService) = conversation {

    name = "guild-setup"
    description = "Conversation that takes place with a user whenever the bot joins a new guild."

    steps {
        step {
            prompt = embed {
                title("Let's Get Setup.")
                color(Color.magenta)
                description("I'm here to help you setup this bot for use on your server. Please follow the prompts." +
                    " If you make a mistake, you can adjust the provided values using commands later.")

                field {
                    name = "Step 1"
                    value = "Please provide the **ID** for the category you'd like me to create report channels in."
                }
            }
            expect = ChannelCategoryArg
        }
        step {
            prompt = embed {
                title("Setup Archive Channel")
                color(Color.magenta)
                description("Now, I need the **ID** of the **channel** you'd like me to send archived reports to.")
            }
            expect = TextChannelArg
        }
        step {
            prompt = embed {
                title("Who can use me?")
                color(Color.magenta)
                setDescription("Now, I need the **Name** of the role you give your staff members so that they can access " +
                    "my moderator functions.")
            }
            expect = RoleArg
        }
        step {
            prompt = embed {
                title("Setup Logging Channel")
                color(Color.magenta)
                description("Now, I need the **ID** of the **channel** you'd like me to log information to.")
            }
            expect = TextChannelArg
        }
    }

    onComplete {
        val reportCategory = it.responses.component1() as Category
        val archiveChannel = it.responses.component2() as TextChannel
        val staffRole = it.responses.component3() as Role
        val loggingChannel = it.responses.component4() as TextChannel

        it.respond(
            when {
                reportCategory.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "report category")
                archiveChannel.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "archive channel")
                staffRole.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "staff role")
                loggingChannel.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "logging channel")
                else -> {
                    val guildConfiguration = GuildConfiguration(it.guildId, reportCategory.id, archiveChannel.id, staffRole.name)
                    guildConfiguration.loggingConfiguration!!.loggingChannel = loggingChannel.id
                    config.guildConfigurations.add(guildConfiguration)
                    persistenceService.save(config)
                    "Successfully configured for use! As the guild owner, you can adjust these values at any time."
                }
            }
        )

        return@onComplete
    }
}
