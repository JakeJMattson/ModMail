package me.aberrantfox.warmbot.preconditions

import me.jakejmattson.kutils.api.annotations.Precondition
import me.jakejmattson.kutils.api.dsl.preconditions.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration

@Precondition
fun produceIsValidGuildPrecondition(configuration: Configuration) = precondition {
    val guildId = it.guild?.id ?: return@precondition Pass

    if (guildId !in configuration.whitelist) return@precondition Fail(Locale.FAIL_GUILD_NOT_WHITELISTED)

    if (!configuration.hasGuildConfig(guildId)) return@precondition Fail(Locale.FAIL_GUILD_NOT_CONFIGURED)

    return@precondition Pass
}