package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.warmbot.listeners.ChannelDeletionListener
import me.aberrantfox.warmbot.listeners.ReportListener
import me.aberrantfox.warmbot.listeners.ResponseListener
import me.aberrantfox.warmbot.services.ReportService
import me.aberrantfox.warmbot.services.loadConfiguration


fun main(args: Array<String>) {
    val config = loadConfiguration()

    if(config == null) {
        println("Please fill in the configuration file (config/config.json)")
        return
    }

    startBot(config.token, "!", "me.aberrantfox.warmbot") {
        val reportService = ReportService(jda, config)
        jda.addEventListener(
                ReportListener(reportService),
                ResponseListener(reportService),
                ChannelDeletionListener(reportService))
    }
}