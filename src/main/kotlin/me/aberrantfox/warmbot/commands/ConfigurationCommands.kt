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
            val eventChannel = it.channel as TextChannel

            if (hasGuildConfiguration(configuration.guildConfigurations, eventChannel.guild.id)) {
                configuration.guildConfigurations.first { g -> g.guildId == eventChannel.guild.id }.reportCategory =
                        reportCategory.id

                saveConfiguration(configuration)
                it.respond("Successfully set report category to :: ${reportCategory.name}")
            } else
                displayNoConfig(it)

            return@execute
        }
    }

    command("setarchivechannel") {
        description = "Set the channel where transcribed reports will be sent when archived."
        expect(TextChannelArg)
        execute {
            val archiveChannel = it.args.component1() as TextChannel

            if (hasGuildConfiguration(configuration.guildConfigurations, archiveChannel.guild.id)) {
                configuration.guildConfigurations.first { g -> g.guildId == archiveChannel.guild.id }.archiveChannel =
                        archiveChannel.id

                saveConfiguration(configuration)
                it.respond("Successfully the archive channel to :: ${archiveChannel.name}")
            } else
                displayNoConfig(it)

            return@execute
        }
    }

    command("setstaffrole") {
        description = "Specify the role required to use this bot."
        expect(WordArg)
        execute {
            val staffRoleName = it.args.component1() as String
            val staffRole = it.jda.getRolesByName(staffRoleName, true).firstOrNull()

            if (staffRole != null) {
                if (hasGuildConfiguration(configuration.guildConfigurations, staffRole.guild.id)) {
                    configuration.guildConfigurations.first { g -> g.guildId == staffRole.guild.id }.staffRoleName =
                            staffRole.name

                    saveConfiguration(configuration)
                    it.respond("Successfully set the staff role to :: ${staffRole.name}")
                } else
                    displayNoConfig(it)
            } else {
                it.respond("Could not find a role named :: $staffRoleName")
            }
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