package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.warmbot.listeners.ChannelDeletionListener
import me.aberrantfox.warmbot.listeners.ReportListener
import me.aberrantfox.warmbot.listeners.ResponseListener
import me.aberrantfox.warmbot.services.ReportService
import me.aberrantfox.warmbot.services.loadConfiguration

object ObjectRegister {
    private val register = HashMap<String, Any>()

    operator fun get(key: String) = register[key]
    operator fun set(key: String, value: Any) = register.put(key, value)
}

fun main(args: Array<String>) {
    val config = loadConfiguration()

    if(config == null) {
        println("Please fill in the configuration file (config/config.json)")
        return
    }

    startBot(config.token, config.prefix, "me.aberrantfox.warmbot") {
        val reportService = ReportService(jda, config)
        jda.addEventListener(
                ReportListener(reportService),
                ResponseListener(reportService, config.prefix),
                ChannelDeletionListener(reportService))

        ObjectRegister["reportService"] = reportService
        ObjectRegister["config"] = config
    }
}