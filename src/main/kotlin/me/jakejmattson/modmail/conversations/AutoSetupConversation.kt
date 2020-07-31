package me.jakejmattson.modmail.conversations

import me.jakejmattson.kutils.api.arguments.BooleanArg
import me.jakejmattson.kutils.api.dsl.conversation.*
import me.jakejmattson.kutils.api.services.ConversationService
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
import net.dv8tion.jda.api.entities.*
import java.util.Timer
import kotlin.concurrent.schedule

class AutoSetupConversation(private val conversationService: ConversationService) : Conversation() {
    @Start
    fun autoSetupConversation(configuration: Configuration, guild: Guild) = conversation {
        val isAutomatic = promptEmbed(BooleanArg("", "yes", "no")) {
            color = infoColor
            simpleTitle = "Automatic Setup"
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

        if (isAutomatic)
            autoSetup(configuration, guild, this)
        else
            Timer().schedule(1500) {
                conversationService.startPrivateConversation<GuildSetupConversation>(user, configuration, guild)
            }
    }

    data class GuildData(var holderCategory: Category?,
                         var archiveChannel: GuildChannel?,
                         var loggingChannel: GuildChannel?,
                         var reportCategory: Category?,
                         var role: Role?) {
        fun isFull() = when {
            holderCategory == null -> false
            archiveChannel == null -> false
            loggingChannel == null -> false
            reportCategory == null -> false
            role == null -> false
            else -> true
        }
    }

    private fun autoSetup(config: Configuration, guild: Guild, stateContainer: ConversationStateContainer) {

        val guildData = GuildData(
            guild.getCategoriesByName(Locale.DEFAULT_HOLDER_CATEGORY_NAME, true).firstOrNull(),
            guild.getTextChannelsByName(Locale.DEFAULT_ARCHIVE_CHANNEL_NAME, true).firstOrNull(),
            guild.getTextChannelsByName(Locale.DEFAULT_LOGGING_CHANNEL_NAME, true).firstOrNull(),
            guild.getCategoriesByName(Locale.DEFAULT_REPORT_CATEGORY_NAME, true).firstOrNull(),
            guild.getRolesByName(Locale.DEFAULT_STAFF_ROLE_NAME, true).firstOrNull()
        )

        if (guildData.holderCategory == null) {
            guild.createCategory(Locale.DEFAULT_HOLDER_CATEGORY_NAME).queue {
                guildData.holderCategory = it
                attemptToFinalize(config, guild, guildData)
            }
        }

        if (guildData.archiveChannel == null) {
            guild.createTextChannel(Locale.DEFAULT_ARCHIVE_CHANNEL_NAME).queue {
                guildData.archiveChannel = it
                attemptToFinalize(config, guild, guildData)
            }
        }

        if (guildData.loggingChannel == null) {
            guild.createTextChannel(Locale.DEFAULT_LOGGING_CHANNEL_NAME).queue {
                guildData.loggingChannel = it
                attemptToFinalize(config, guild, guildData)
            }
        }

        if (guildData.reportCategory == null) {
            guild.createCategory(Locale.DEFAULT_REPORT_CATEGORY_NAME).queue {
                guildData.reportCategory = it
                attemptToFinalize(config, guild, guildData)
            }
        }

        if (guildData.role == null) {
            guild.createRole().setName(Locale.DEFAULT_STAFF_ROLE_NAME).queue {
                guildData.role = it
                attemptToFinalize(config, guild, guildData)
            }
        }

        attemptToFinalize(config, guild, guildData)
        stateContainer.respond(Locale.GUILD_SETUP_SUCCESSFUL)
    }

    private fun attemptToFinalize(config: Configuration, guild: Guild, data: GuildData) {
        if (!data.isFull())
            return

        val holderCategory = data.holderCategory!!
        val archiveChannel = data.archiveChannel!!
        val loggingChannel = data.loggingChannel!!
        val reportCategory = data.reportCategory!!
        val role = data.role!!

        archiveChannel.manager.setParent(holderCategory).queue()
        loggingChannel.manager.setParent(holderCategory).queue()

        val logConfig = LoggingConfiguration(loggingChannel.idLong)
        val guildConfiguration = GuildConfiguration("!", reportCategory.idLong, archiveChannel.idLong, role.idLong, logConfig)
        config.guildConfigurations[guild.idLong] = guildConfiguration
        config.save()
    }
}