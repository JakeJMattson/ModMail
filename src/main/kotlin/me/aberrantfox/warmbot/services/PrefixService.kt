package me.aberrantfox.warmbot.services

import me.jakejmattson.kutils.api.Discord
import me.jakejmattson.kutils.api.annotations.Service

@Service
class PrefixService(private val configuration: Configuration, private val discord: Discord) {
    fun setPrefix(prefix: String) {
        configuration.prefix = prefix
        discord.configuration.prefix { prefix }
    }
}