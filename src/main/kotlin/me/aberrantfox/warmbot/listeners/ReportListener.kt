package me.aberrantfox.warmbot.listeners

import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ReportListener(val reportService: ReportService)  : ListenerAdapter() {
    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if(event.author.isBot) {
            return
        }
        val user = event.author
        var message = event.message.contentRaw

        var attachString = "\n"

        if(event.message.attachments.isNotEmpty()) {
            attachString += event.message.attachments.map { it.url }.reduce { a, b -> "$a\n $b" }
        }

        message += attachString

        if(reportService.hasReportChannel(user.id)) {
            reportService.receiveFromUser(user, message)
        } else {
            reportService.addReport(user)
            reportService.receiveFromUser(user, message)
        }
    }
}