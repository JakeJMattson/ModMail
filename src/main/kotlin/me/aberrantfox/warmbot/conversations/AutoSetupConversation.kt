package me.aberrantfox.warmbot.conversations

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.kjdautils.internal.command.arguments.ChoiceArg
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.messages.*
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.managers.GuildController
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

    val wasBindSuccessful = bindExistingServerEntities(config, persistenceService, guild, defaults)

    if (wasBindSuccessful) return@apply this.respond(Locale.messages.GUILD_SETUP_SUCCESSFUL)

    guildController.createCategory(defaults.DEFAULT_HOLDER_CATEGORY_NAME).queue {
        it as Category

        it.createTextChannel(defaults.DEFAULT_ARCHIVE_CHANNEL_NAME).queue { archiveChannel ->
            it.createTextChannel(defaults.DEFAULT_LOGGING_CHANNEL_NAME).queue { loggingChannel ->
                guildController.createCategory(defaults.DEFAULT_REPORT_CATEGORY_NAME).queue { reportCategory ->
                    guildController.createRole().setName(defaults.DEFAULT_STAFF_ROLE_NAME).queue { role ->
                        createConfig(config, persistenceService, guildId, reportCategory, archiveChannel, loggingChannel, role)
                        this.respond(defaults.GUILD_SETUP_SUCCESSFUL)
                    }
                }
            }
        }
    }
}

private fun bindExistingServerEntities(config: Configuration, persistenceService: PersistenceService, guild: Guild, defaults: Messages): Boolean {
    val holderCategory = guild.getCategoriesByName(defaults.DEFAULT_HOLDER_CATEGORY_NAME, true).firstOrNull()
        ?: return false

    val archiveChannel = holderCategory.channels.firstOrNull { it.name == defaults.DEFAULT_ARCHIVE_CHANNEL_NAME }
        ?: return false

    val loggingChannel = holderCategory.channels.firstOrNull { it.name == defaults.DEFAULT_LOGGING_CHANNEL_NAME }
        ?: return false

    val reportCategory = guild.getCategoriesByName(defaults.DEFAULT_REPORT_CATEGORY_NAME, true).firstOrNull()
        ?: return false

    val role = guild.getRolesByName(defaults.DEFAULT_STAFF_ROLE_NAME, true).firstOrNull()
        ?: return false

    createConfig(config, persistenceService, guild.id, reportCategory, archiveChannel, loggingChannel, role)
    return true
}

private fun createConfig(config: Configuration, persistenceService: PersistenceService, guildId: String,
                         reportCategory: Channel, archiveChannel: Channel, loggingChannel: Channel, role: Role) {

    val logConfig = LoggingConfiguration(loggingChannel.id)
    val guildConfiguration = GuildConfiguration(guildId, reportCategory.id, archiveChannel.id, role.name, logConfig)
    config.guildConfigurations.add(guildConfiguration)
    persistenceService.save(config)
}