package me.aberrantfox.warmbot.conversations

import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.arguments.BooleanArg
import me.jakejmattson.kutils.api.dsl.conversation.*
import me.jakejmattson.kutils.api.dsl.embed.embed
import me.jakejmattson.kutils.api.services.*
import net.dv8tion.jda.api.entities.*
import java.util.Timer
import kotlin.concurrent.schedule

class AutoSetupConversation(private val persistenceService: PersistenceService,
                            private val conversationService: ConversationService) : Conversation() {
    @Start
    fun autoSetupConversation(configuration: Configuration, guild: Guild) = conversation {
        val isAutomatic = blockingPrompt(BooleanArg("", "yes", "no")) {
            embed {
                color = infoColor
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
            autoSetup(configuration, guild, persistenceService, this)
        else
            Timer().schedule(1500) {
                conversationService.startPrivateConversation<GuildSetupConversation>(user, configuration, guild)
            }
    }

    private fun autoSetup(config: Configuration, guild: Guild,
                          persistenceService: PersistenceService,
                          stateContainer: ConversationStateContainer) {
        val guildData = mutableListOf(
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

    private fun attemptToFinalize(config: Configuration, persistenceService: PersistenceService, guild: Guild, data: MutableList<Any?>) {
        if (data.any { it == null })
            return

        val holderCategory = data[0] as Category
        val archiveChannel = data[1] as GuildChannel
        val loggingChannel = data[2] as GuildChannel
        val commandChannel = data[3] as GuildChannel
        val reportCategory = data[4] as Category
        val role = data[5] as Role

        archiveChannel.manager.setParent(holderCategory).queue()
        loggingChannel.manager.setParent(holderCategory).queue()
        commandChannel.manager.setParent(holderCategory).queue()

        val staffChannels = arrayListOf(commandChannel.id)
        val logConfig = LoggingConfiguration(loggingChannel.id)
        val guildConfiguration = GuildConfiguration(guild.id, reportCategory.id, archiveChannel.id, role.name, staffChannels, logConfig)
        config.guildConfigurations.add(guildConfiguration)
        persistenceService.save(config)
    }
}