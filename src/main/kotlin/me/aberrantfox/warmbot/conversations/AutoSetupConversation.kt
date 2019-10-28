package me.aberrantfox.warmbot.conversations

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.arguments.BooleanArg
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.kjdautils.internal.services.ConversationService
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.entities.*
import java.awt.Color
import java.util.Timer
import kotlin.concurrent.schedule

@Convo
fun autoSetupConversation(configuration: Configuration, persistenceService: PersistenceService, conversationService: ConversationService) = conversation("auto-setup") {

    val isAutomatic = blockingPrompt(BooleanArg("", "yes", "no")) {
        embed {
            color = Color.magenta
            title = "Automatic Setup"
            description = "Would you like to automatically configure this guild for use?"
            field {
                name = "Automatic Setup"
                value = "An automatic setup will generate all required channels and roles for you automatically. " +
                    "This process will also load the channels by name if they already exist."
                inline = false
            }
            field {
                name = "Manual Setup"
                value = "A manual setup will prompt you for each of the required fields. " +
                    "You will need to create each of these channels if they do not exist. " +
                    "This will also require you to know how to get ID's from channels."
                inline = false
            }
            field {
                name = "Options"
                value = "Say **Yes** to initialize automatic setup mode, or **No** to start manual setup mode."
                inline = false
            }
        }
    }

    if (isAutomatic)
        autoSetup(configuration, persistenceService, this)
    else
        Timer().schedule(1500) {
            conversationService.createConversation(user, guild, "guild-setup")
        }
}

private fun autoSetup(config: Configuration, persistenceService: PersistenceService, stateContainer: ConversationStateContainer) {
    val guild = stateContainer.guild

    val guildData = arrayListOf(
        guild.getCategoriesByName(Locale.DEFAULT_HOLDER_CATEGORY_NAME, true).firstOrNull(),
        guild.getTextChannelsByName(Locale.DEFAULT_ARCHIVE_CHANNEL_NAME, true).firstOrNull(),
        guild.getTextChannelsByName(Locale.DEFAULT_LOGGING_CHANNEL_NAME, true).firstOrNull(),
        guild.getTextChannelsByName(Locale.DEFAULT_COMMAND_CHANNEL_NAME, true).firstOrNull(),
        guild.getCategoriesByName(Locale.DEFAULT_REPORT_CATEGORY_NAME, true).firstOrNull(),
        guild.getRolesByName(Locale.DEFAULT_STAFF_ROLE_NAME, true).firstOrNull()
    )

    if (guildData[0] == null) {
        guild.createCategory(Locale.DEFAULT_HOLDER_CATEGORY_NAME).queue {
            guildData[0] = it
            attemptToFinalize(config, persistenceService, guild, guildData)
        }
    }

    if (guildData[1] == null) {
        guild.createTextChannel(Locale.DEFAULT_ARCHIVE_CHANNEL_NAME).queue {
            guildData[1] = it
            attemptToFinalize(config, persistenceService, guild, guildData)
        }
    }

    if (guildData[2] == null) {
        guild.createTextChannel(Locale.DEFAULT_LOGGING_CHANNEL_NAME).queue {
            guildData[2] = it
            attemptToFinalize(config, persistenceService, guild, guildData)
        }
    }

    if (guildData[3] == null) {
        guild.createTextChannel(Locale.DEFAULT_COMMAND_CHANNEL_NAME).queue {
            guildData[3] = it
            attemptToFinalize(config, persistenceService, guild, guildData)
        }
    }

    if (guildData[4] == null) {
        guild.createCategory(Locale.DEFAULT_REPORT_CATEGORY_NAME).queue {
            guildData[4] = it
            attemptToFinalize(config, persistenceService, guild, guildData)
        }
    }

    if (guildData[5] == null) {
        guild.createRole().setName(Locale.DEFAULT_STAFF_ROLE_NAME).queue {
            guildData[5] = it
            attemptToFinalize(config, persistenceService, guild, guildData)
        }
    }

    attemptToFinalize(config, persistenceService, guild, guildData)
    stateContainer.respond(Locale.GUILD_SETUP_SUCCESSFUL)
}

private fun attemptToFinalize(config: Configuration, persistenceService: PersistenceService, guild: Guild, data: ArrayList<Any?>) {
    if (data.any { it == null })
        return

    val holderCategory = data[0] as Category
    val archiveChannel = data[1] as GuildChannel
    val loggingChannel = data[2] as GuildChannel
    val commandChannel = data[3] as GuildChannel
    val reportCategory =  data[4] as Category
    val role =  data[5] as Role

    archiveChannel.manager.setParent(holderCategory).queue()
    loggingChannel.manager.setParent(holderCategory).queue()
    commandChannel.manager.setParent(holderCategory).queue()

    val staffChannels = arrayListOf(commandChannel.id)
    val logConfig = LoggingConfiguration(loggingChannel.id)
    val guildConfiguration = GuildConfiguration(guild.id, reportCategory.id, archiveChannel.id, role.name, staffChannels, logConfig)
    config.guildConfigurations.add(guildConfiguration)
    persistenceService.save(config)
}