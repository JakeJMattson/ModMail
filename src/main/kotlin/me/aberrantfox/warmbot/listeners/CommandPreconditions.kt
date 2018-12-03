package me.aberrantfox.warmbot.listeners

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.TextChannel

fun produceIsStaffMemberPrecondition(configuration: Configuration) = { event: CommandEvent ->

    if (event.channel is TextChannel) {
        val textChannel = event.channel as TextChannel
        if (!configuration.hasGuildConfig(textChannel.guild.id)) {
            Pass
        } else {
            val relevantGuildConfiguration = configuration.getGuildConfig(textChannel.guild.id)!!
            val relevantGuild = event.jda.getGuildById(textChannel.guild.id)
            val staffRole = relevantGuild.getRolesByName(relevantGuildConfiguration.staffRoleName, true).first()
            val memberAuthor = relevantGuild.getMember(event.author)
            if (memberAuthor.roles.contains(staffRole)) {
                Pass
            } else {
                Fail("You do not have the staff role.")
            }
        }
    } else {
        Fail("Did you really think I'd let you do that? \uD83E\uDD14")
    }
}

fun produceIsGuildOwnerPrecondition() = { event: CommandEvent ->

    val command = event.container.commands[event.commandStruct.commandName]
    if (command == null)
        Pass
    else if (command.category == "Configuration") {
        if (event.channel is TextChannel) {
            val textChannel = event.channel as TextChannel
            val relevantGuild = event.jda.getGuildById(textChannel.guild.id)
            if (relevantGuild.owner.user.id == event.author.id) {
                Pass
            } else {
                Fail("You must be the owner of the guild in order to use this command.")
            }
        } else {
            Fail("This command cannot be used in a private message, it must be invoked from a channel.")
        }
    } else {
        Pass
    }
}