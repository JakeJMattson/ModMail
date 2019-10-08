package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.Precondition
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.api.entities.TextChannel

private const val Category = "Owner"

@Precondition
fun produceIsBotOwnerPrecondition(configuration: Configuration) = exit@{ event: CommandEvent<*> ->
    val command = event.container.commands[event.commandStruct.commandName] ?: return@exit Pass

    if (command.category != Category) return@exit Pass

    if (event.channel !is TextChannel) return@exit Fail(Locale.messages.FAIL_TEXT_CHANNEL_ONLY)

    if (configuration.ownerId != event.author.id) return@exit Fail(Locale.messages.FAIL_MUST_BE_BOT_OWNER)

    return@exit Pass
}