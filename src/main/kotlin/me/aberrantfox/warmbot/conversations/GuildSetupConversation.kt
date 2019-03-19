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
            expect = ChannelCategoryArg
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
        val staffRole = it.responses.component4() as Role

        it.respond(
            when {
                reportCategory.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "report category")
                archiveChannel.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "archive channel")
                loggingChannel.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "logging channel")
                staffRole.guild.id != it.guildId -> Locale.inject({ FAIL_GUILD_SETUP }, "field" to "staff role")
                else -> {
                    val guildConfiguration = GuildConfiguration(it.guildId, reportCategory.id, archiveChannel.id, staffRole.name)
                    guildConfiguration.loggingConfiguration.loggingChannel = loggingChannel.id
                    config.guildConfigurations.add(guildConfiguration)
                    persistenceService.save(config)
                    Locale.messages.GUILD_SETUP_SUCCESSFUL
                }
            }
        )

        return@onComplete
    }
}
