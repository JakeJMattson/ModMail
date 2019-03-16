package me.aberrantfox.warmbot.conversations

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.kjdautils.internal.command.arguments.ChoiceArg
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.managers.*
import java.awt.Color

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
                    name = "INFO"
                    value = "An automatic setup will generate all required channels and roles for you automatically. " +
                        "Say **Yes** to setup the server automatically, or **No** to start manual setup mode."
                    inline = false
                }
            }
            expect = ChoiceArg("", "Yes", "Y", "No", "N")
        }
    }

    onComplete {
        val choice = (it.responses.component1() as String)[0]

        when (choice) {
            'Y' -> autoSetup(configuration, persistenceService, it)
            //'N' -> conversationService.createConversation(it.userId, it.guildId, "guild-setup")
        }
    }
}

private fun autoSetup(config: Configuration, persistenceService: PersistenceService, stateContainer: ConversationStateContainer) = stateContainer.apply {
    val jda = this.jda
    val guild = jda.getGuildById(guildId)
    val guildController = GuildController(guild)
    val defaults = Locale.messages

    val guildData = arrayOfNulls<Any>(5)
    guildData[0] = guild.getCategoriesByName(defaults.DEFAULT_HOLDER_CATEGORY_NAME, true).firstOrNull()
    guildData[1] = guild.getTextChannelsByName(defaults.DEFAULT_ARCHIVE_CHANNEL_NAME, true).firstOrNull()
    guildData[2] = guild.getTextChannelsByName(defaults.DEFAULT_LOGGING_CHANNEL_NAME, true).firstOrNull()
    guildData[3] = guild.getCategoriesByName(defaults.DEFAULT_REPORT_CATEGORY_NAME, true).firstOrNull()
    guildData[4] = guild.getRolesByName(defaults.DEFAULT_STAFF_ROLE_NAME, true).firstOrNull()

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
        guildController.createCategory(defaults.DEFAULT_REPORT_CATEGORY_NAME).queue {
            guildData[3] = it
            attemptToFinalize(config, persistenceService, guildId, guildData)
        }
    }

    if (guildData[4] == null) {
        guildController.createRole().setName(defaults.DEFAULT_STAFF_ROLE_NAME).queue {
            guildData[4] = it
            attemptToFinalize(config, persistenceService, guildId, guildData)
        }
    }

    attemptToFinalize(config, persistenceService, guildId, guildData)
    this.respond(defaults.GUILD_SETUP_SUCCESSFUL)
}

private fun attemptToFinalize(config: Configuration, persistenceService: PersistenceService, guildId: String, data: Array<Any?>) {
    if (data.any { it == null })
        return

    val holderCategory = data.component1() as Category
    val archiveChannel = data.component2() as Channel
    val loggingChannel = data.component3() as Channel
    val reportCategory =  data.component4() as Category
    val role =  data.component5() as Role

    ChannelManager(archiveChannel).setParent(holderCategory).queue()
    ChannelManager(loggingChannel).setParent(holderCategory).queue()

    createConfig(config, persistenceService, guildId, reportCategory, archiveChannel, loggingChannel, role)
}

private fun createConfig(config: Configuration, persistenceService: PersistenceService, guildId: String,
                         reportCategory: Channel, archiveChannel: Channel, loggingChannel: Channel, role: Role) {

    val logConfig = LoggingConfiguration(loggingChannel.id)
    val guildConfiguration = GuildConfiguration(guildId, reportCategory.id, archiveChannel.id, role.name, logConfig)
    config.guildConfigurations.add(guildConfiguration)
    persistenceService.save(config)
}