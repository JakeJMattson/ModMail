package me.aberrantfox.warmbot.listeners

import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ReportListener(val reportService: ReportService)  : ListenerAdapter() {
    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if(event.author.isBot) {
            return
        }

        val user = event.author.id
        val message = event.message.contentRaw

        if(reportService.hasReportChannel(user)) {
            reportService.receiveFromUser(user, message)
        } else {
            reportService.addReport(event.author)
            reportService.receiveFromUser(user, message)
        }
    }
}