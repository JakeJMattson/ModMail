package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.*

@CommandSet("configuration")
fun configurationCommands(conversationService: ConversationService, configuration: Configuration) = commands {
    command("setreportcategory") {
        description = Locale.messages.SET_REPORT_CATEGORY_DESCRIPTION
        expect(ChannelCategoryArg)
        execute {
            val reportCategory = it.args.component1() as Category
            val guildConfig = configuration.getGuildConfig(reportCategory.guild.id)

            if (guildConfig == null) {
                displayNoConfig(it)
                return@execute
            }

            guildConfig.reportCategory = reportCategory.id
            configuration.save()
            val response = Locale.inject({REPORT_ARCHIVE_SUCCESSFUL}, "reportName" to reportCategory.name)
            it.respond(response)

            return@execute
        }
    }

    command("setarchivechannel") {
        description = Locale.messages.SET_ARCHIVE_CHANNEL_DESCRIPTION
        expect(TextChannelArg)
        execute {
            val archiveChannel = it.args.component1() as TextChannel
            val guildConfig = configuration.getGuildConfig(archiveChannel.guild.id)

            if (guildConfig == null) {
                displayNoConfig(it)
                return@execute
            }

            guildConfig.archiveChannel = archiveChannel.id
            configuration.save()
            val response = Locale.inject({ ARCHIVE_CHANNEL_SET_SUCCESSFUL }, "archiveChannel" to archiveChannel.name)
            it.respond(response)

            return@execute
        }
    }

    command("setstaffrole") {
        description = "Specify the role required to use this bot."
        expect(WordArg)
        execute {
            val staffRoleName = it.args.component1() as String
            val staffRole = it.jda.getRolesByName(staffRoleName, true).firstOrNull()

            if (staffRole == null) {
                it.respond("Could not find a role named :: $staffRoleName")
                return@execute
            }

            val guildConfig = configuration.getGuildConfig(it.message.guild.id)

            if (guildConfig == null) {
                displayNoConfig(it)
                return@execute
            }
            
            guildConfig.staffRoleName = staffRole.name
            configuration.save()
            it.respond("Successfully set the staff role to :: ${staffRole.name}")

            return@execute
        }
    }

    command("setup") {
        description = "Initiate a setup conversation to set all required values for this bot."
        execute {
            val guildId = it.guild!!.id

            if (!configuration.hasGuildConfig(guildId))
                conversationService.createConversation(it.author.id, guildId, "guild-setup")
            else
                it.respond(
                        "I'm already setup for use in this guild, please use the appropriate commands to change specific settings.")
            return@execute
        }
    }
}

fun displayNoConfig(event: CommandEvent)
        = event.respond("No guild configuration found, please go through the setup process before using this command.")