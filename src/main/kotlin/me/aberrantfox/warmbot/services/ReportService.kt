package me.aberrantfox.warmbot.services

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.*
import me.aberrantfox.kjdautils.extensions.stdlib.sanitiseMentions
import me.aberrantfox.kjdautils.internal.logging.DefaultLogger
import me.aberrantfox.warmbot.extensions.fullContent
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.*
import java.awt.Color
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

data class Report(
    @Expose val userId: String,
    @Expose val channelId: String,
    @Expose val guildId: String,
    @Expose val messages: MutableMap<String, String>,
    @Expose var queuedMessageId: String? = null,
    @Expose var shouldAutoClose: Boolean,
            var timer: Timer? = null)

data class QueuedReport(val messages: Vector<String> = Vector(), val user: String)

class ReportService(val jda: JDA, private val config: Configuration, val loggingService: LoggingService) {

    private val gson = GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create()
    private val reportDir = File("reports/")
    private val reports = Vector<Report>()
    private val queuedReports = Vector<QueuedReport>()

    fun isReportChannel(channelId: String) = reports.any { it.channelId == channelId }
    fun hasReportChannel(userId: String) = reports.any { it.userId == userId } || queuedReports.any { it.user == userId }
    fun getReportByChannel(channelId: String): Report = reports.first { it.channelId == channelId }
    fun getReportByUserId(userId: String): Report = reports.first { it.userId == userId }
    fun getReportsFromGuild(guildId: String) = reports.filter { it.guildId == guildId }

    fun addReport(user: User, guild: Guild, firstMessage: Message) {
        val guildConfiguration = config.getGuildConfig(guild.id)!!
        val reportCategory = jda.getCategoryById(guildConfiguration.reportCategory)

        if (reports.none { it.userId == user.id }) {
            if (getReportsFromGuild(guild.id).size == config.maxOpenReports)
                return
        }

        if (guild.textChannels.size >= 250) {
            return
        }

        reportCategory.createTextChannel(user.name).queue { channel ->
            createReportChannel(channel as TextChannel, user, firstMessage, guild)
        }
    }

    fun addReport(report: Report) {
        reports.add(report)
        writeReportToFile(report)
    }

    fun receiveFromUser(userObject: User, message: Message) {
        val user = userObject.id
        val safeMessage = message.fullContent().trim().sanitiseMentions()

        if (hasReportChannel(user)) {
            val report = getReportByUserId(user)
            jda.getTextChannelById(report.channelId).sendMessage(safeMessage).queue()
            report.queuedMessageId = message.id

            if (report.timer != null) {
                report.timer!!.cancel()
                report.timer = null
            }

            return
        }

        val queued = queuedReports.firstOrNull { it.user == user }

        if (queued == null) {
            val vector = Vector<String>()
            vector.add(safeMessage)
            queuedReports.add(QueuedReport(vector, user))
        } else {
            queued.messages.addElement(safeMessage)
        }
    }

    fun sendToUser(channelId: String, message: Message) {
        val report = getReportByChannel(channelId)

        jda.getUserById(report.userId).sendPrivateMessage(message.fullContent(), DefaultLogger())
        report.queuedMessageId = message.id

        if (report.timer != null)
            report.timer!!.cancel()

        if (!report.shouldAutoClose)
            return

        report.timer = Timer()

        val millis = config.getGuildConfig(report.guildId)!!.autoCloseSeconds * 1000

        report.timer!!.schedule(millis) {
            loggingService.autoClose(report)
            jda.getTextChannelById(report.channelId).delete().queue()
        }
    }

    fun buildGuildChoiceEmbed(commonGuilds: List<Guild>) =
        embed {
            setColor(Color.CYAN)
            setAuthor("Please choose which server's staff you'd like to contact.")
            setThumbnail(jda.selfUser.avatarUrl)
            description("Respond with the number that correlates with the desired server to get started.")
            addBlankField(true)

            commonGuilds.forEachIndexed { index, guild ->
                field {
                    name = "$index) ${guild.name}"
                    inline = false
                }
            }
        }

    fun buildReportOpenedEmbed(guildObject: Guild)= embed {
            setColor(Color.PINK)
            setAuthor("You've successfully opened a report with the staff of ${guildObject.name}")
            description("Someone will respond shortly, please be patient.")
            setThumbnail(guildObject.iconUrl)
        }

    fun closeReport(report: Report) {
        sendReportClosedEmbed(report)
        removeReport(report)
    }

    private fun sendReportClosedEmbed(report: Report) =
        jda.getUserById(report.userId).sendPrivateMessage(embed {
            setColor(Color.LIGHT_GRAY)
            setAuthor("The staff of ${jda.getGuildById(report.guildId).name} have closed this report.")
            setDescription("If you continue to reply, a new report will be created.")
        })

    private fun removeReport(report: Report) {
        reports.remove(report)

        val file = reportDir.listFiles().firstOrNull { it.name.startsWith(report.channelId) } ?: return
        file.delete()
    }

    fun loadReports() {
        if (!config.recoverReports && reportDir.exists()) {
            reportDir.deleteRecursively()
            return
        }

        if (!reportDir.exists()) {
            reportDir.mkdirs()
            return
        }

        cleanDeadReports()
    }

    fun writeReportToFile(report: Report) {
        if (config.recoverReports)
            File("$reportDir/${report.channelId}.json").writeText(gson.toJson(report))
    }

    private fun cleanDeadReports() = reportDir.listFiles().forEach {
        val report = gson.fromJson(it.readText(), Report::class.java)
        if (jda.getTextChannelById(report.channelId) != null) reports.add(report) else it.delete()
    }

    private fun createReportChannel(channel: TextChannel, user: User, firstMessage: Message, guild: Guild) {
        val openingMessage = embed {
            addField("New Report Opened!", "${user.descriptor()} :: ${user.asMention}", false)
            setColor(Color.green)
        }

        channel.sendMessage(openingMessage).queue()
        queuedReports.first { it.user == user.id }.messages.forEach {
            channel.sendMessage(it).queue()
        }

        val newReport = Report(user.id, channel.id, guild.id, ConcurrentHashMap(), firstMessage.id)
        addReport(newReport)
        loggingService.memberOpen(newReport)

        queuedReports.removeAll { it.user == user.id }
    }
}
