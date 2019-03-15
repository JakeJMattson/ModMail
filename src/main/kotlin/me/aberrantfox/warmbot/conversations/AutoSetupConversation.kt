package me.aberrantfox.warmbot.conversations

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.kjdautils.internal.command.arguments.ChoiceArg
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.messages.Locale
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
                    value = "An automatic setup will generate all required channels and roles for you automatically."
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
    val guildController = GuildController(jda.getGuildById(this.guildId))

    guildController.createCategory("WARMBOT").queue {
        it as Category

        it.createTextChannel("archive").queue { archiveChannel ->
            it.createTextChannel("logging").queue { loggingChannel ->
                guildController.createCategory("WARMBOT-REPORTS").queue { reportCategory ->
                    guildController.createRole().setName("Staff").queue { role ->
                        createConfig(config, this.guildId, reportCategory, archiveChannel, loggingChannel, role)
                        persistenceService.save(config)
                        this.respond(Locale.messages.GUILD_SETUP_SUCCESSFUL)
                    }
                }
            }
        }
    }
}

private fun createConfig(config: Configuration, guildId: String, reportCategory: Channel,
                         archiveChannel: Channel, loggingChannel: Channel, role: Role) {

    val logConfig = LoggingConfiguration(loggingChannel.id)
    val guildConfiguration = GuildConfiguration(guildId, reportCategory.id, archiveChannel.id, role.name, logConfig)
    config.guildConfigurations.add(guildConfiguration)
}