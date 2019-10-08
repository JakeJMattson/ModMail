package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.Precondition
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration

@Precondition
fun produceIsValidGuildPrecondition(configuration: Configuration) = exit@{ event: CommandEvent<*> ->
    val guildId = event.guild?.id ?: return@exit Fail(Locale.messages.FAIL_COMMAND_NOT_IN_GUILD)

    if (guildId !in configuration.whitelist) return@exit Fail(Locale.messages.FAIL_GUILD_NOT_WHITELISTED)

    if (!configuration.hasGuildConfig(guildId)) return@exit Fail(Locale.messages.FAIL_GUILD_NOT_CONFIGURED)

    return@exit Pass
}