package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.descriptor
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.extensions.stdlib.sanitiseMentions
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.User
import java.awt.Color
import java.util.*

data class Report(val user: String, val channelId: String, val guildId: String)

data class QueuedReport(val messages: Vector<String> = Vector(), val user: String)

class ReportService(val jda: JDA, private val config: Configuration) {

    private val reports = Vector<Report>()
    private val queuedReports = Vector<QueuedReport>()

    fun isReportChannel(channelId: String) = reports.any { it.channelId == channelId }
    fun hasReportChannel(userId: String) = reports.any { it.user == userId } || queuedReports.any { it.user == userId }
    fun getReportByChannel(channelId: String): Report = reports.first { it.channelId == channelId }

    fun addReport(user: User, guild: Guild) {

        val guildConfiguration = config.guildConfigurations.first { g -> g.guildId == guild.id }
        val reportCategory = jda.getCategoryById(guildConfiguration.reportCategory)

        if (reports.none { it.user == user.id }) {
            if (reports.filter { it.guildId == guild.id }.size == config.maxOpenReports)
                return
        }

        if (guild.textChannels.size >= 250) {
            return
        }

        reportCategory.createTextChannel(user.name).queue { channel ->
            queuedReports.first { it.user == user.id }.messages.forEach {
                (channel as TextChannel).sendMessage(it).queue()
            }
            reports.add(Report(user.id, channel.id, guild.id))
            queuedReports.removeAll { it.user == user.id }
        }
    }

    fun removeReport(channel: String) {
        reports.removeAll { it.channelId == channel }
    }

    fun receiveFromUser(userObject: User, message: String) {
        val user = userObject.id
        val safeMessage = message.sanitiseMentions()
        if (reports.any { it.user == user }) {
            val report = reports.first { it.user == user }
            jda.getTextChannelById(report.channelId).sendMessage(safeMessage).queue()

            return
        }

        val queued = queuedReports.firstOrNull { it.user == user }

        if (queued == null) {
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

        if (report != null) {
            jda.getUserById(report.user).sendPrivateMessage(message)
        }
    }

    fun buildGuildChoiceEmbed(userObject: User): MessageEmbed {
        return embed {
            setColor(Color.CYAN)
            setAuthor("Please choose which server's staff you'd like to contact.")
            setThumbnail(userObject.jda.selfUser.avatarUrl)
            description("Respond with the number that correlates with the desired server to get started.")
            addBlankField(true)

            userObject.mutualGuilds.forEachIndexed { index, guild ->
                if (config.guildConfigurations.filter { g -> g.guildId == guild.id }.any()) {
                    field {
                        name = "$index) ${guild.name}"
                        inline = false
                    }
                }
            }
        }
    }

    fun buildReportOpenedEmbed(guildObject: Guild): MessageEmbed {
        return embed {
            setColor(Color.PINK)
            setAuthor("You've successfully opened a report with the staff of ${guildObject.name}")
            description("Someone will respond shortly, please be patient.")
            setThumbnail(guildObject.iconUrl)
        }
    }

    fun sendReportClosedEmbed(report: Report) {
        jda.getUserById(report.user).sendPrivateMessage(embed {
            setColor(Color.LIGHT_GRAY)
            setAuthor("The staff of ${jda.getGuildById(report.guildId).name} have closed this report.")
            setDescription("If you continue to reply, a new report will be created.")
        })
    }

    fun getCommonGuilds(userObject: User): List<Guild> {
        return userObject.mutualGuilds.filter { g -> g.id in config.guildConfigurations.associateBy { it.guildId } }
    }
}
