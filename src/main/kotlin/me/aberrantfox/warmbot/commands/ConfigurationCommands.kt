package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*

@CommandSet("configuration")
fun configurationCommands(conversationService: ConversationService, configuration: Configuration) = commands {

    command("setreportcategory") {
        description = "Set the category where new reports will be opened."
        expect(ChannelCategoryArg)
        execute {
            val reportCategory = it.args.component1() as Category
            val guildConfig = getGuildConfig(configuration.guildConfigurations, reportCategory.guild.id)

            if (guildConfig == null) {
                displayNoConfig(it)
                return@execute
            }

            guildConfig.reportCategory = reportCategory.id
            saveConfiguration(configuration)
            it.respond("Successfully set report category to :: ${reportCategory.name}")

            return@execute
        }
    }

    command("setarchivechannel") {
        description = "Set the channel where transcribed reports will be sent when archived."
        expect(TextChannelArg)
        execute {
            val archiveChannel = it.args.component1() as TextChannel
            val guildConfig = getGuildConfig(configuration.guildConfigurations, archiveChannel.guild.id)

            if (guildConfig == null) {
                displayNoConfig(it)
                return@execute
            }

            guildConfig.archiveChannel = archiveChannel.id
            saveConfiguration(configuration)
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

            val guildConfig = getGuildConfig(configuration.guildConfigurations, it.guild!!.id)

            if (guildConfig == null) {
                displayNoConfig(it)
                return@execute
            }
            
            guildConfig.staffRoleName = staffRole.name
            saveConfiguration(configuration)
            it.respond("Successfully set the staff role to :: ${staffRole.name}")

            return@execute
        }
    }

    command("setup") {
        description = "Initiate a setup conversation to set all required values for this bot."
        execute {
            val guildId = it.guild!!.id

            if (!hasGuildConfiguration(configuration.guildConfigurations, guildId))
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