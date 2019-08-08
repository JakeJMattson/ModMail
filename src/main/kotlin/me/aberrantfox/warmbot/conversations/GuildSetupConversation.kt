package me.aberrantfox.warmbot.conversations

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.entities.*
import java.awt.Color

@Convo
fun guildSetupConversation(config: Configuration, persistenceService: PersistenceService) = conversation {

    name = "guild-setup"
    description = "Conversation to set configuration fields."

    steps {
        step {
            prompt = embed {
                color(Color.magenta)
                title("Let's Get Setup")
                description("I'm here to help you setup this bot for use on your server. Please follow the prompts." +
                    " If you make a mistake, you can adjust the provided values using commands later.")

                field {
                    name = "Report Category"
                    value = "Enter the **Category ID** of the category where new reports will be created."
                }
            }
            expect = CategoryArg
        }
        step {
            prompt = embed {
                color(Color.magenta)
                title("Archive Channel")
                description("Enter the **Channel ID** of the channel where archived reports will be sent.")
            }
            expect = TextChannelArg
        }
        step {
            prompt = embed {
                color(Color.magenta)
                title("Logging Channel")
                description("Enter the **Channel ID** of the channel where information will be logged.")
            }
            expect = TextChannelArg
        }
        step {
            prompt = embed {
                color(Color.magenta)
                title("Command Channel")
                description("Enter the **Channel ID** where commands can be used. More can be added later.")
            }
            expect = TextChannelArg
        }
        step {
            prompt = embed {
                color(Color.magenta)
                title("Required Role")
                setDescription("Enter the **Role Name** of the role required to give commands to this bot.")
            }
            expect = RoleArg
        }
    }

    onComplete {
        val reportCategory = it.responses.component1() as Category
        val archiveChannel = it.responses.component2() as TextChannel
        val loggingChannel = it.responses.component3() as TextChannel
        val commandChannel = it.responses.component4() as TextChannel
        val staffRole = it.responses.component5() as Role

        it.respond(when {
            reportCategory.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "report category")
            archiveChannel.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "archive channel")
            loggingChannel.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "logging channel")
            commandChannel.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "command channel")
            staffRole.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "staff role")
            else -> {
                val staffChannels = arrayListOf(commandChannel.id)
                val logConfig = LoggingConfiguration(loggingChannel.id)
                config.guildConfigurations.add(
                    GuildConfiguration(it.guildId, reportCategory.id, archiveChannel.id, staffRole.name, staffChannels, logConfig))

                persistenceService.save(config)
                Locale.messages.GUILD_SETUP_SUCCESSFUL
            }
        })
    }
}
