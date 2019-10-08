package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.Precondition
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.messages.Locale
import net.dv8tion.jda.api.entities.TextChannel

private const val Category = "Configuration"

@Precondition
fun produceIsGuildOwnerPrecondition() = exit@{ event: CommandEvent<*> ->
    val command = event.container.commands[event.commandStruct.commandName] ?: return@exit Pass

    if (command.category != Category) return@exit Pass

    if (event.channel !is TextChannel) return@exit Fail(Locale.messages.FAIL_TEXT_CHANNEL_ONLY)

    val textChannel = event.channel as TextChannel

    if (textChannel.guild.ownerId != event.author.id) return@exit Fail(Locale.messages.FAIL_MUST_BE_GUILD_OWNER)

    return@exit Pass
}