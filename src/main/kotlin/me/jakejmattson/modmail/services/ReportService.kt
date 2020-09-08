package me.jakejmattson.modmail.services

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.*
import com.gitlab.kordlib.core.behavior.channel.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.rest.Image
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.count
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.extensions.cleanContent
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class Report(val userId: String,
                  val channelId: String,
                  val guildId: String,
                  val messages: MutableMap<String, String> = mutableMapOf()) {

    suspend fun toLiveReport(api: Kord): LiveReport? {
        val user = userId.toSnowflake()?.let { api.getUser(it) } ?: return null
        val channel = channelId.toSnowflake()?.let { api.getChannel(it) } as? TextChannel ?: return null
        val guild = guildId.toSnowflake()?.let { api.getGuild(it) } ?: return null

        return LiveReport(user, channel, guild)
    }
}

data class LiveReport(val user: User,
                      val channel: TextChannel,
                      val guild: Guild)

private data class QueuedReport(val messages: Vector<String> = Vector(), val user: Snowflake)

private val reports = Vector<Report>()

suspend fun UserBehavior.toLiveReport() = findReport()?.toLiveReport(kord)
fun UserBehavior.findReport() = reports.firstOrNull { it.userId == id.value }
fun MessageChannelBehavior.findReport() = reports.firstOrNull { it.channelId == id.value }
suspend fun MessageChannelBehavior.toLiveReport() = findReport()?.toLiveReport(kord)

@Service
class ReportService(private val config: Configuration,
                    private val loggingService: LoggingService,
                    private val discord: Discord) {
    init {
        GlobalScope.launch {
            reportsFolder.listFiles()?.forEach {
                val report = Json.decodeFromString<Report>(it.readText())
                val channel = report.channelId.toSnowflake()?.let { discord.api.getChannel(it) }

                if (channel != null) reports.add(report) else it.delete()
            }
        }
    }

    suspend fun createReport(user: User, guild: Guild) {
        if (guild.channels.count() >= 250) return

        val reportCategory = config[guild.id.longValue]?.getLiveReportCategory(guild.kord) ?: return

        val reportChannel = guild.createTextChannel {
            name = user.username
            parentId = reportCategory.id
        }

        createReportChannel(reportChannel, user, guild)
    }

    fun addReport(report: Report) {
        reports.add(report)
        writeReportToFile(report)
    }

    suspend fun receiveFromUser(message: Message) {
        val user = message.author!!
        val safeMessage = message.cleanContent(discord)

        with(user.findReport()) {
            val liveReport = this?.toLiveReport(message.kord) ?: return@with

            if (safeMessage.isEmpty()) return

            val newMessage = liveReport.channel.createMessage(safeMessage)
            messages[message.id.value] = newMessage.id.value
        }
    }

    fun writeReportToFile(report: Report) =
        File("$reportsFolder/${report.channelId}.json").writeText(Json.encodeToString(report))

    private suspend fun createReportChannel(channel: TextChannel, user: User, guild: Guild) {
        println("Creating channel")

        channel.createEmbed {
            author {
                name = user.tag
                icon = user.avatar.url
                url = user.profileLink
            }
        }

        //TODO send opening messages

        val newReport = Report(user.id.value, channel.id.value, guild.id.value, ConcurrentHashMap())
        addReport(newReport)

        user.sendPrivateMessage {
            title = "You've successfully opened a report with the staff of ${guild.name}"
            description = "Someone will respond shortly, please be patient."

            thumbnail {
                url = guild.getIconUrl(Image.Format.PNG)!!
            }
        }

        loggingService.memberOpen(newReport)
    }
}

suspend fun Report.close(api: Kord) {
    release(api)
    removeReport(this)
    toLiveReport(api)?.let { sendReportClosedEmbed(it) }
}

private fun removeReport(report: Report) {
    reports.remove(report)
    reportsFolder.listFiles()?.firstOrNull { it.name.startsWith(report.channelId.toString()) }?.delete()
}

private suspend fun sendReportClosedEmbed(report: LiveReport) =
    report.user.sendPrivateMessage {
        title = "The staff of ${report.guild.name} have closed this report."
        description = "If you continue to reply, a new report will be created."
    }

