package me.aberrantfox.warmbot.services

import com.google.gson.GsonBuilder
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.*
import me.aberrantfox.kjdautils.internal.logging.DefaultLogger
import me.aberrantfox.warmbot.extensions.*
import net.dv8tion.jda.core.entities.*
import java.awt.Color
import java.io.File
import java.util.Vector
import java.util.concurrent.ConcurrentHashMap

data class Report(val userId: String,
                  val channelId: String,
                  val guildId: String,
                  val messages: MutableMap<String, String>,
                  var queuedMessageId: String? = null)

data class QueuedReport(val messages: Vector<String> = Vector(), val user: String)

@Service
class ReportService(private val config: Configuration,
                    private val loggingService: LoggingService,
                    jdaInitializer: JdaInitializer) {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val reportDir = File("reports/")
    private val reports = Vector<Report>()
    private val queuedReports = Vector<QueuedReport>()

    init { loadReports() }

    fun isReportChannel(channelId: String) = reports.any { it.channelId == channelId }
    fun hasReportChannel(userId: String) = reports.any { it.userId == userId } || queuedReports.any { it.user == userId }
    fun getReportByChannel(channelId: String): Report = reports.first { it.channelId == channelId }
    fun getReportByUserId(userId: String): Report = reports.first { it.userId == userId }
    fun getReportsFromGuild(guildId: String) = reports.filter { it.guildId == guildId }
    fun getCommonGuilds(userObject: User): List<Guild> = userObject.mutualGuilds.filter { it.id in config.guildConfigurations.associateBy { it.guildId } }

    private fun loadReports() {
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

    private fun cleanDeadReports() = reportDir.listFiles().forEach {
        val report = gson.fromJson(it.readText(), Report::class.java)
        if (report.channelId.idToTextChannel() != null) reports.add(report) else it.delete()
    }

    fun addReport(user: User, guild: Guild, firstMessage: Message) {
        if (getReportsFromGuild(guild.id).size == config.maxOpenReports || guild.textChannels.size >= 250) return

        val reportCategory = config.getGuildConfig(guild.id)?.reportCategory!!.idToCategory()
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
        val safeMessage = message.cleanContent()

        if (hasReportChannel(user)) {
            val report = getReportByUserId(user)
            report.channelId.idToTextChannel().sendMessage(safeMessage).queue()
            report.queuedMessageId = message.id

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

        report.userId.idToUser().sendPrivateMessage(message.fullContent(), DefaultLogger())
        report.queuedMessageId = message.id
    }

    fun buildGuildChoiceEmbed(commonGuilds: List<Guild>) =
        embed {
            setColor(Color.CYAN)
            setAuthor("Please choose which server's staff you'd like to contact.")
            setThumbnail(selfUser().avatarUrl)
            description("Respond with the number that correlates with the desired server to get started.")

            commonGuilds.forEachIndexed { index, guild ->
                field {
                    name = "$index) ${guild.name}"
                    inline = false
                }
            }
        }

    fun buildReportOpenedEmbed(guildObject: Guild) =
        embed {
            setColor(Color.PINK)
            setAuthor("You've successfully opened a report with the staff of ${guildObject.name}")
            description("Someone will respond shortly, please be patient.")
            setThumbnail(guildObject.iconUrl)
        }

    fun writeReportToFile(report: Report) {
        if (config.recoverReports)
            File("$reportDir/${report.channelId}.json").writeText(gson.toJson(report))
    }

    private fun createReportChannel(channel: TextChannel, user: User, firstMessage: Message, guild: Guild) {
        val openingMessage = embed {
            addField("New Report Opened!", "${user.descriptor()} :: ${user.asMention}", false)
            setThumbnail(user.avatarUrl)
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

    fun closeReport(report: Report) {
        sendReportClosedEmbed(report)
        removeReport(report)
    }

    private fun sendReportClosedEmbed(report: Report) =
        report.userId.idToUser().sendPrivateMessage(embed {
            setColor(Color.LIGHT_GRAY)
            setAuthor("The staff of ${report.guildId.idToGuild().name} have closed this report.")
            setDescription("If you continue to reply, a new report will be created.")
        })

    private fun removeReport(report: Report) {
        reports.remove(report)
        reportDir.listFiles().firstOrNull { it.name.startsWith(report.channelId) }?.delete()
    }
}