package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration

@Precondition
fun produceIsValidGuildPrecondition(configuration: Configuration) = exit@{ event: CommandEvent ->
    val guild = event.guild!!

    if (guild.id !in configuration.whitelist) return@exit Fail(Locale.messages.NOT_WHITELISTED)

    if (!configuration.hasGuildConfig(guild.id)) return@exit Fail(Locale.messages.NOT_CONFIGURED)

    Pass
}