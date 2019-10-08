package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.messages.Locale
import net.dv8tion.jda.api.entities.TextChannel

private const val Category = "Configuration"

@Precondition
fun produceIsGuildOwnerPrecondition() = precondition {
    val command = it.container.commands[it.commandStruct.commandName] ?: return@precondition Pass

    if (command.category != Category) return@precondition Pass

    if (it.channel !is TextChannel) return@precondition Fail(Locale.messages.FAIL_TEXT_CHANNEL_ONLY)

    val textChannel = it.channel as TextChannel

    if (textChannel.guild.ownerId != it.author.id) return@precondition Fail(Locale.messages.FAIL_MUST_BE_GUILD_OWNER)

    return@precondition Pass
}