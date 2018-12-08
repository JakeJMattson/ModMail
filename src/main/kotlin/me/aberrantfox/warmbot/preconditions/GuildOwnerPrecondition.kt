package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.internal.command.Fail
import me.aberrantfox.kjdautils.internal.command.Pass
import net.dv8tion.jda.core.entities.TextChannel

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