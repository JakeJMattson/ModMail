package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration

@Precondition
fun produceIsValidGuildPrecondition(configuration: Configuration) = exit@{ event: CommandEvent ->
    val guildId = event.guild!!.id

    if (guildId !in configuration.whitelist) return@exit Fail(Locale.messages.FAIL_GUILD_NOT_WHITELISTED)

    if (!configuration.hasGuildConfig(guildId)) return@exit Fail(Locale.messages.FAIL_GUILD_NOT_CONFIGURED)

    return@exit Pass
}