package me.jakejmattson.modmail.services

import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.dsl.embed.embed
import me.jakejmattson.discordkt.api.extensions.jda.*
import me.jakejmattson.modmail.extensions.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import java.io.File
import java.util.Vector
import java.util.concurrent.ConcurrentHashMap

data class Report(val userId: Long,
                  val channelId: Long,
                  val guildId: Long,
                  val messages: MutableMap<String, String>) {

    fun toLiveReport(jda: JDA): LiveReport? {
        val user = jda.getUserById(userId) ?: return null
        val channel = jda.getTextChannelById(channelId) ?: return null
        val guild = jda.getGuildById(guildId) ?: return null

        return LiveReport(user, channel, guild)
    }
}

data class LiveReport(val user: User,
                      val channel: TextChannel,
                      val guild: Guild) {
    val member = user.toMember(guild)
}

private data class QueuedReport(val messages: Vector<String> = Vector(), val user: String)

private val reports = Vector<Report>()
private val queuedReports = Vector<QueuedReport>()

fun User.toLiveReport() = findReport()?.toLiveReport(jda)
fun User.findReport() = reports.firstOrNull { it.userId == this.idLong }
fun MessageChannel.findReport() = reports.firstOrNull { it.channelId == this.idLong }
fun MessageChannel.toLiveReport() = findReport()?.toLiveReport(jda)

@Service
class ReportService(private val config: Configuration,
                    private val loggingService: LoggingService,
                    discord: Discord) {
    init {
        loadReports(discord.jda)
    }

    fun getCommonGuilds(userObject: User) = userObject.mutualGuilds.filter { config.guildConfigurations[it.idLong] != null }

    private fun loadReports(jda: JDA) =
        reportsFolder.listFiles()?.forEach {
            val report = gson.fromJson(it.readText(), Report::class.java)
            val channel = jda.getTextChannelById(report.channelId)

            if (channel != null) reports.add(report) else it.delete()
        }

    fun createReport(user: User, guild: Guild) {
        if (guild.textChannels.size >= 250) return

        val reportCategoryId = config[guild.idLong]?.reportCategory!!
        val reportCategory = user.jda.getCategoryById(reportCategoryId) ?: return

        reportCategory.createTextChannel(user.name).queue { channel ->
            createReportChannel(channel as TextChannel, user, guild)
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

        with(user.findReport()) {
            val liveReport = this?.toLiveReport(message.jda) ?: return@with

            if (safeMessage.isEmpty()) return

            liveReport.channel.sendMessage(safeMessage).queue {
                messages[message.id] = it.id
            }

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

    fun writeReportToFile(report: Report) =
        File("$reportsFolder/${report.channelId}.json").writeText(gson.toJson(report))

    private fun createReportChannel(channel: TextChannel, user: User, guild: Guild) {
        val userMessage = embed {
            color = successColor
            simpleTitle = "You've successfully opened a report with the staff of ${guild.name}"
            description = "Someone will respond shortly, please be patient."
            thumbnail = guild.iconUrl
        }

        val openingMessage = embed {
            addField("New Report Opened!", user.descriptor(), false)
            thumbnail = user.effectiveAvatarUrl
            color = successColor
        }

        channel.sendMessage(openingMessage).queue()
        queuedReports.first { it.user == user.id }.messages.forEach {
            if (it.isNotEmpty())
                channel.sendMessage(it).queue()
        }

        val newReport = Report(user.idLong, channel.idLong, guild.idLong, ConcurrentHashMap())
        addReport(newReport)

        user.sendPrivateMessage(userMessage)
        loggingService.memberOpen(newReport)

        queuedReports.removeAll { it.user == user.id }
    }
}

fun Report.close(jda: JDA) {
    release(jda)
    removeReport(this)
    toLiveReport(jda)?.let { sendReportClosedEmbed(it) }
}

private fun removeReport(report: Report) {
    reports.remove(report)
    reportsFolder.listFiles()?.firstOrNull { it.name.startsWith(report.channelId.toString()) }?.delete()
}

private fun sendReportClosedEmbed(report: LiveReport) =
    report.user.sendPrivateMessage(embed {
        color = failureColor
        simpleTitle = "The staff of ${report.guild.name} have closed this report."
        description = "If you continue to reply, a new report will be created."
    })

