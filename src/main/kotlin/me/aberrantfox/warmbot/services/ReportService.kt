package me.aberrantfox.warmbot.services

import com.google.gson.GsonBuilder
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.*
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
                  var queuedMessageId: String? = null) {
    fun reportToUser() = userId.idToUser()
    fun reportToMember() = userId.idToUser()?.toMember(guildId.idToGuild())
    fun reportToChannel() = channelId.idToTextChannel()
    fun reportToGuild() = guildId.idToGuild()
}

data class QueuedReport(val messages: Vector<String> = Vector(), val user: String)

private val reports = Vector<Report>()
private val queuedReports = Vector<QueuedReport>()
private val reportDir = File("reports/")

fun User.hasReportChannel() = reports.any { it.userId == this.id } || queuedReports.any { it.user == this.id }
fun User.userToReport() = reports.firstOrNull { it.userId == this.id }
fun MessageChannel.isReportChannel() = reports.any { it.channelId == this.id }
fun MessageChannel.channelToReport() = reports.first { it.channelId == this.id }

@Service
class ReportService(private val config: Configuration,
                    private val loggingService: LoggingService,
                    jdaInitializer: JdaInitializer) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        loadReports()
    }

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

        reportDir.listFiles().forEach {
            val report = gson.fromJson(it.readText(), Report::class.java)
            if (report.reportToChannel() != null) reports.add(report) else it.delete()
        }
    }

    fun createReport(user: User, guild: Guild, firstMessage: Message) {
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

    fun receiveFromUser(message: Message) {
        val user = message.author
        val userID = user.id
        val safeMessage = message.cleanContent()

        with(user.userToReport()) {
            this ?: return@with

            user.toMember(reportToGuild()) ?: return message.addFailReaction()

            if (safeMessage.isEmpty()) return

            reportToChannel().sendMessage(safeMessage).queue()
            queuedMessageId = message.id

            return
        }

        val queued = queuedReports.firstOrNull { it.user == userID }

        if (queued == null) {
            val vector = Vector<String>()
            vector.add(safeMessage)
            queuedReports.add(QueuedReport(vector, userID))
        } else {
            queued.messages.addElement(safeMessage)
        }
    }

    fun writeReportToFile(report: Report) {
        if (config.recoverReports)
            File("$reportDir/${report.channelId}.json").writeText(gson.toJson(report))
    }

    private fun createReportChannel(channel: TextChannel, user: User, firstMessage: Message, guild: Guild) {
        val userMessage = embed {
            setColor(Color.PINK)
            setAuthor("You've successfully opened a report with the staff of ${guild.name}")
            description("Someone will respond shortly, please be patient.")
            setThumbnail(guild.iconUrl)
        }

        val openingMessage = embed {
            addField("New Report Opened!", "${user.descriptor()} :: ${user.asMention}", false)
            setThumbnail(user.avatarUrl)
            setColor(Color.green)
        }

        channel.sendMessage(openingMessage).queue()
        queuedReports.first { it.user == user.id }.messages.forEach {
            if (it.isNotEmpty())
                channel.sendMessage(it).queue()
        }

        val newReport = Report(user.id, channel.id, guild.id, ConcurrentHashMap(), firstMessage.id)
        addReport(newReport)

        user.sendPrivateMessage(userMessage)
        loggingService.memberOpen(newReport)

        queuedReports.removeAll { it.user == user.id }
    }
}

fun Report.close() {
    this.release()
    sendReportClosedEmbed(this)
    removeReport(this)
}

private fun sendReportClosedEmbed(report: Report) =
    report.reportToUser().sendPrivateMessage(embed {
        setColor(Color.LIGHT_GRAY)
        setAuthor("The staff of ${report.reportToGuild().name} have closed this report.")
        setDescription("If you continue to reply, a new report will be created.")
    })

private fun removeReport(report: Report) {
    reports.remove(report)
    reportDir.listFiles().firstOrNull { it.name.startsWith(report.channelId) }?.delete()
}