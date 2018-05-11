package me.aberrantfox.warmbot.listeners

import me.aberrantfox.warmbot.extensions.fullContent
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ReportListener(val reportService: ReportService)  : ListenerAdapter() {
    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if(event.author.isBot) {
            return
        }
        val user = event.author
        val message = event.message.fullContent()

        if(reportService.hasReportChannel(user.id)) {
            reportService.receiveFromUser(user, message)
        } else {
            reportService.addReport(user)
            reportService.receiveFromUser(user, message)
        }
    }
}