package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration

@Precondition
fun produceIsValidGuildPrecondition(configuration: Configuration) = precondition {
    val guildId = it.guild?.id ?: return@precondition Fail(Locale.messages.FAIL_COMMAND_NOT_IN_GUILD)

    if (guildId !in configuration.whitelist) return@precondition Fail(Locale.messages.FAIL_GUILD_NOT_WHITELISTED)

    if (!configuration.hasGuildConfig(guildId)) return@precondition Fail(Locale.messages.FAIL_GUILD_NOT_CONFIGURED)

    return@precondition Pass
}