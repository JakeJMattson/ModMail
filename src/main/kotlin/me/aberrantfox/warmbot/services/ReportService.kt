package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.extensions.jda.descriptor
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.extensions.stdlib.sanitiseMentions
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.User
import java.util.*

data class Report(val user: String, val channelId: String)

data class QueuedReport(val messages: Vector<String> = Vector(), val user: String)

class ReportService(val jda: JDA, config: Configuration) {
    private val reportCategory = jda.getCategoryById(config.reportCategory)
    private val reports = Vector<Report>()
    private val queuedReports = Vector<QueuedReport>()

    fun isReportChannel(channelId: String) = reports.any { it.channelId == channelId }

    fun hasReportChannel(userId: String) = reports.any { it.user == userId } || queuedReports.any { it.user == userId }

    fun addReport(user: User) {
        if (reports.none { it.user == user.id }) {
            reportCategory.createTextChannel(user.id).queue { channel ->
                queuedReports.first { it.user == user.id }.messages.forEach {
                    (channel as TextChannel).sendMessage(it).queue()
                }
                reports.add(Report(user.id, channel.id))
                queuedReports.removeAll { it.user == user.id }
            }
        }
    }

    fun removeReport(channel: String) {
        reports.removeAll { it.channelId == channel }
    }

    fun receiveFromUser(userObject: User, message: String) {
        val user = userObject.id
        val safeMessage = message.sanitiseMentions()
        if(reports.any { it.user == user }) {
            val report = reports.first { it.user == user }
            jda.getTextChannelById(report.channelId).sendMessage(safeMessage).queue()

            return
        }

        val queued = queuedReports.firstOrNull { it.user == user }

        if(queued == null) {
            val vector = Vector<String>()
            vector.add("${userObject.descriptor()} :: ${userObject.asMention}")
            vector.add(safeMessage.sanitiseMentions())
            queuedReports.add(QueuedReport(vector, user))
        } else {
            queued.messages.addElement(safeMessage.sanitiseMentions())
        }
    }

    fun sendToUser(channelId: String, message: String) {
        val report = reports.firstOrNull { it.channelId == channelId }

        if(report != null) {
            jda.getUserById(report.user).sendPrivateMessage(message)
        }
    }
}