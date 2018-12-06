package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.*
import kotlin.math.roundToLong

@CommandSet("configuration")
fun configurationCommands(conversationService: ConversationService, configuration: Configuration) = commands {

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

    command("setreportcategory") {
        description = "Set the category where new reports will be opened."
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
            it.respond("Successfully set report category to :: ${reportCategory.name}")

            return@execute
        }
    }

    command("setarchivechannel") {
        description = "Set the channel where transcribed reports will be sent when archived."
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
            it.respond("Successfully the archive channel to :: ${archiveChannel.name}")

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

    command("setautoclose") {
        description = "Set the amount of time required for a report to close automatically from inactivity."
        expect(TimeStringArg)
        execute {
            val time = it.args.component1()
            val seconds = (time as Double).roundToLong()
            val guildConfig = configuration.getGuildConfig(it.message.guild.id)

            if (guildConfig == null) {
                displayNoConfig(it)
                return@execute
            }

            guildConfig.autoCloseSeconds = seconds
            configuration.save()
            it.respond("Successfully set the auto close timer to $seconds seconds")

            return@execute
        }
    }
}

fun displayNoConfig(event: CommandEvent)
        = event.respond("No guild configuration found, please go through the setup process before using this command.")