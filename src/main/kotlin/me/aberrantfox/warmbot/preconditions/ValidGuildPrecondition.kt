package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import me.jakejmattson.kutils.api.annotations.Precondition
import me.jakejmattson.kutils.api.dsl.preconditions.*

@Precondition
fun produceIsValidGuildPrecondition(configuration: Configuration) = precondition {
    val guildId = it.guild?.id ?: return@precondition Pass

    if (!configuration.hasGuildConfig(guildId)) return@precondition Fail(Locale.FAIL_GUILD_NOT_CONFIGURED)

    return@precondition Pass
}