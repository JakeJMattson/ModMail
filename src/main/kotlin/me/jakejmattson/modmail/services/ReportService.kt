package me.jakejmattson.modmail.services

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.*
import com.gitlab.kordlib.core.behavior.channel.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.rest.Image
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.count
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.extensions.cleanContent
import java.awt.Color
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class Report(val userId: String,
                  val channelId: String,
                  val guildId: String,
                  val messages: MutableMap<String, String> = mutableMapOf()) {

    suspend fun toLiveReport(api: Kord): LiveReport? {
        val user = userId.toSnowflakeOrNull()?.let { api.getUser(it) } ?: return null
        val channel = channelId.toSnowflakeOrNull()?.let { api.getChannel(it) } as? TextChannel ?: return null
        val guild = guildId.toSnowflakeOrNull()?.let { api.getGuild(it) } ?: return null

        return LiveReport(user, channel, guild)
    }
}

data class LiveReport(val user: User,
                      val channel: TextChannel,
                      val guild: Guild)

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
                val channel = report.channelId.toSnowflakeOrNull()?.let { discord.api.getChannel(it) }

                if (channel != null) reports.add(report) else it.delete()
            }
        }
    }

    suspend fun createReport(user: User, guild: Guild) {
        if (guild.channels.count() >= 250) return

        val reportCategory = config[guild.id.longValue]?.getLiveReportCategory(guild.kord) ?: return

        createReportChannel(reportCategory, user, guild)
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

    private suspend fun createReportChannel(category: Category, user: User, guild: Guild) {
        val reportChannel = guild.createTextChannel {
            name = user.username
            parentId = category.id
        }

        reportChannel.createEmbed {
            author {
                name = user.tag
                icon = user.avatar.url
                url = user.profileLink
            }
        }

        //TODO send opening messages

        val newReport = Report(user.id.value, reportChannel.id.value, guild.id.value, ConcurrentHashMap())
        addReport(newReport)

        user.sendPrivateMessage {
            color = Color.GREEN

            field {
                name = "A report has been created."
                value = "Someone will respond shortly, please be patient."
            }

            author {
                name = guild.name
                icon = guild.getIconUrl(Image.Format.PNG)
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
        color = Color.RED

        field {
            name = "This report has been closed."
            value = "Any response will create a new report"
        }

        author {
            name = report.guild.name
            icon = report.guild.getIconUrl(Image.Format.PNG)
        }
    }

