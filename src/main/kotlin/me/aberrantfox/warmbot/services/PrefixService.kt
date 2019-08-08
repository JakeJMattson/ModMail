package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.KConfiguration

@Service
class PrefixService(configuration: Configuration, private val kConfiguration: KConfiguration) {
    init {
        setPrefix(configuration.prefix)
    }

    fun setPrefix(prefix: String) {
        kConfiguration.prefix = prefix
    }
}