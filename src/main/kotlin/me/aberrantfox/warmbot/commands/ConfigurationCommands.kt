package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.kjdautils.internal.command.arguments.WordArg
import me.aberrantfox.kjdautils.internal.command.arguments.TextChannelArg
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.Category
import net.dv8tion.jda.core.entities.TextChannel


@CommandSet("Configuration")
fun configurationCommands(conversationService: ConversationService, configuration: Configuration) = commands {

    command("setreportcategory") {
        expect(ChannelCategoryArg)
        execute {
            val reportCategory = it.args.component1() as Category
            val eventChannel = it.channel as TextChannel

            if (hasGuildConfiguration(configuration.guildConfigurations, eventChannel.guild.id)) {
                configuration.guildConfigurations.first { g -> g.guildId == eventChannel.guild.id }.reportCategory =
                        reportCategory.id

                saveConfiguration(configuration)
                it.respond("Successfully set report category to :: ${reportCategory.name}")
            } else {
                it.respond(
                        "No guild configuration found, please go through the setup process before using this command.")
            }

            return@execute
        }
    }

    command("setarchivechannel") {
        expect(TextChannelArg)
        execute {
            val archiveChannel = it.args.component1() as TextChannel

            if (hasGuildConfiguration(configuration.guildConfigurations, archiveChannel.guild.id)) {
                configuration.guildConfigurations.first { g -> g.guildId == archiveChannel.guild.id }.archiveChannel =
                        archiveChannel.id

                saveConfiguration(configuration)
                it.respond("Successfully the archive channel to :: ${archiveChannel.name}")
            } else {
                it.respond(
                        "No guild configuration found, please go through the setup process before using this command.")
            }
            return@execute
        }
    }

    command("setstaffrole") {
        expect(WordArg)
        execute {
            val staffRoleName = it.args.component1() as String
            val staffRole = it.jda.getRolesByName(staffRoleName, true).first()

            if (staffRole != null) {
                if (hasGuildConfiguration(configuration.guildConfigurations, staffRole.guild.id)) {
                    configuration.guildConfigurations.first { g -> g.guildId == staffRole.guild.id }.staffRoleName =
                            staffRole.name

                    saveConfiguration(configuration)
                    it.respond("Successfully the staff role to :: ${staffRole.name}")
                } else {
                    it.respond(
                            "No guild configuration found, please go through the setup process before using this command.")
                }
            } else {
                it.respond("Could not find a role named :: $staffRoleName")
            }
            return@execute
        }
    }

    command("setup") {
        execute {
            val eventChannel = it.channel as TextChannel

            if (!hasGuildConfiguration(configuration.guildConfigurations, eventChannel.guild.id))
                conversationService.createConversation(it.author.id, eventChannel.guild.id, "guild-setup")
            else
                it.respond(
                        "I'm already setup for use in this guild, please use the appropriate commands to change specific settings.")
            return@execute
        }
    }
}

