package me.aberrantfox.warmbot.conversations

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.kjdautils.internal.command.arguments.ChoiceArg
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.extensions.idToGuild
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.managers.*
import java.awt.Color
import java.util.Timer
import kotlin.concurrent.schedule

@Convo
fun autoSetupConversation(configuration: Configuration, persistenceService: PersistenceService, conversationService: ConversationService) = conversation {

    name = "auto-setup"
    description = "Conversation to ask user whether or not they'd like automatic setup."

    steps {
        step {
            prompt = embed {
                color(Color.magenta)
                title("Automatic Setup")
                description("Would you like to automatically configure this guild for use?")
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
            expect = ChoiceArg("", "Yes", "Y", "No", "N")
        }
    }

    onComplete { stateContainer ->
        val choice = (stateContainer.responses.first() as String).first()

        when (choice) {
            'Y' -> autoSetup(configuration, persistenceService, stateContainer)
            'N' -> Timer().schedule(1500) {
                conversationService.createConversation(stateContainer.userId, stateContainer.guildId, "guild-setup")
            }
        }
    }
}

private fun autoSetup(config: Configuration, persistenceService: PersistenceService, stateContainer: ConversationStateContainer) = stateContainer.apply {
    val guild = guildId.idToGuild()
    val guildController = GuildController(guild)
    val defaults = Locale.messages

    val guildData = arrayListOf(
        guild.getCategoriesByName(defaults.DEFAULT_HOLDER_CATEGORY_NAME, true).firstOrNull(),
        guild.getTextChannelsByName(defaults.DEFAULT_ARCHIVE_CHANNEL_NAME, true).firstOrNull(),
        guild.getTextChannelsByName(defaults.DEFAULT_LOGGING_CHANNEL_NAME, true).firstOrNull(),
        guild.getTextChannelsByName(defaults.DEFAULT_COMMAND_CHANNEL_NAME, true).firstOrNull(),
        guild.getCategoriesByName(defaults.DEFAULT_REPORT_CATEGORY_NAME, true).firstOrNull(),
        guild.getRolesByName(defaults.DEFAULT_STAFF_ROLE_NAME, true).firstOrNull()
    )

    if (guildData[0] == null) {
        guildController.createCategory(defaults.DEFAULT_HOLDER_CATEGORY_NAME).queue {
            guildData[0] = it
            attemptToFinalize(config, persistenceService, guildId, guildData)
        }
    }

    if (guildData[1] == null) {
        guildController.createTextChannel(defaults.DEFAULT_ARCHIVE_CHANNEL_NAME).queue {
            guildData[1] = it
            attemptToFinalize(config, persistenceService, guildId, guildData)
        }
    }

    if (guildData[2] == null) {
        guildController.createTextChannel(defaults.DEFAULT_LOGGING_CHANNEL_NAME).queue {
            guildData[2] = it
            attemptToFinalize(config, persistenceService, guildId, guildData)
        }
    }

    if (guildData[3] == null) {
        guildController.createTextChannel(defaults.DEFAULT_COMMAND_CHANNEL_NAME).queue {
            guildData[3] = it
            attemptToFinalize(config, persistenceService, guildId, guildData)
        }
    }

    if (guildData[4] == null) {
        guildController.createCategory(defaults.DEFAULT_REPORT_CATEGORY_NAME).queue {
            guildData[4] = it
            attemptToFinalize(config, persistenceService, guildId, guildData)
        }
    }

    if (guildData[5] == null) {
        guildController.createRole().setName(defaults.DEFAULT_STAFF_ROLE_NAME).queue {
            guildData[5] = it
            attemptToFinalize(config, persistenceService, guildId, guildData)
        }
    }

    attemptToFinalize(config, persistenceService, guildId, guildData)
    this.respond(defaults.GUILD_SETUP_SUCCESSFUL)
}

private fun attemptToFinalize(config: Configuration, persistenceService: PersistenceService, guildId: String, data: ArrayList<Any?>) {
    if (data.any { it == null })
        return

    val holderCategory = data[0] as Category
    val archiveChannel = data[1] as Channel
    val loggingChannel = data[2] as Channel
    val commandChannel = data[3] as Channel
    val reportCategory =  data[4] as Category
    val role =  data[5] as Role

    ChannelManager(archiveChannel).setParent(holderCategory).queue()
    ChannelManager(loggingChannel).setParent(holderCategory).queue()
    ChannelManager(commandChannel).setParent(holderCategory).queue()

    val staffChannels = arrayListOf(commandChannel.id)
    val logConfig = LoggingConfiguration(loggingChannel.id)
    val guildConfiguration = GuildConfiguration(guildId, reportCategory.id, archiveChannel.id, role.name, staffChannels, logConfig)
    config.guildConfigurations.add(guildConfiguration)
    persistenceService.save(config)
}