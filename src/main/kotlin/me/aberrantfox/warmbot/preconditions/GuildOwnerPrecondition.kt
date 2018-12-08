package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.internal.command.Fail
import me.aberrantfox.kjdautils.internal.command.Pass
import me.aberrantfox.warmbot.messages.Locale
import net.dv8tion.jda.core.entities.TextChannel

private const val Category = "Configuration"

fun produceIsGuildOwnerPrecondition() = exit@{ event: CommandEvent ->
    val command = event.container.commands[event.commandStruct.commandName] ?: return@exit Pass

    if(command.category != Category) return@exit Pass

    if(event.channel !is TextChannel) return@exit Fail(Locale.messages.FAIL_TEXT_CHANNEL_ONLY)

    val textChannel = event.channel as TextChannel
    val relevantGuild = event.jda.getGuildById(textChannel.guild.id)

    if(relevantGuild.owner.user.id == event.author.id) return@exit Pass

    return@exit Fail(Locale.messages.FAIL_MUST_BE_GUILD_OWNER)
}