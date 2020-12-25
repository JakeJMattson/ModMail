package me.jakejmattson.modmail.services

import dev.kord.common.entity.Snowflake
import dev.kord.common.kColor
import dev.kord.core.*
import dev.kord.core.behavior.*
import dev.kord.core.behavior.channel.*
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.*
import dev.kord.rest.Image
import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.count
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.extensions.*
import java.awt.Color
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class Report(val userId: Snowflake,
                  val channelId: Snowflake,
                  val guildId: Snowflake,
                  val messages: MutableMap<Snowflake, Snowflake> = mutableMapOf()) {

    suspend fun toLiveReport(api: Kord): LiveReport? {
        val user = api.getUser(userId) ?: return null
        val guild = api.getGuild(guildId) ?: return null
        val channel = guild.getChannelOfOrNull<TextChannel>(channelId) ?: return null

        return LiveReport(user, channel, guild)
    }
}

data class LiveReport(val user: User, val channel: TextChannel, val guild: Guild)

private val reports = Vector<Report>()

suspend fun UserBehavior.toLiveReport() = findReport()?.toLiveReport(kord)
fun UserBehavior.findReport() = reports.firstOrNull { it.userId == id }
fun MessageChannelBehavior.findReport() = reports.firstOrNull { it.channelId == id }
suspend fun MessageChannelBehavior.toLiveReport() = findReport()?.toLiveReport(kord)
fun GuildBehavior.getReports() = reports.filter { it.guildId == id }

@Service
class ReportService(private val config: Configuration,
                    private val loggingService: LoggingService,
                    private val discord: Discord) {
    init {
        GlobalScope.launch {
            reportsFolder.listFiles()?.forEach {
                val report = Json.decodeFromString<Report>(it.readText())
                val channel = discord.api.getChannel(report.channelId)

                if (channel != null) reports.add(report) else it.delete()
            }
        }
    }

    suspend fun createReport(user: User, guild: Guild) {
        if (guild.channels.count() >= 250) return

        val reportCategory = config[guild.id]?.getLiveReportCategory(guild.kord) ?: return

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
            messages[message.id] = newMessage.id
        }
    }

    fun writeReportToFile(report: Report) =
        File("$reportsFolder/${report.channelId}.json").writeText(Json.encodeToString(report))

    private suspend fun createReportChannel(category: Category, user: User, guild: Guild) {
        val reportChannel = guild.createTextChannel(user.username) {
            parentId = category.id
        }

        user.asMember(guild.id).reportOpenEmbed(reportChannel, false)

        val newReport = Report(user.id, reportChannel.id, guild.id, ConcurrentHashMap())
        addReport(newReport)

        user.sendPrivateMessage {
            color = Color.GREEN.kColor

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

suspend fun Report.close(kord: Kord) {
    release(kord)
    removeReport(this)
    sendReportClosedEmbed(this, kord)
}

private fun removeReport(report: Report) {
    reports.remove(report)
    reportsFolder.listFiles()?.firstOrNull { it.name.startsWith(report.channelId.asString) }?.delete()
}

suspend fun Member.reportOpenEmbed(channel: TextChannel, detain: Boolean = false) = channel.createEmbed {
    author {
        name = username
        icon = avatar.url
    }

    description = descriptor()
    color = if (detain) Color.RED.kColor else Color.GREEN.kColor
}

private suspend fun sendReportClosedEmbed(report: Report, kord: Kord) {
    val guild = kord.getGuild(report.guildId) ?: return
    val user = kord.getUser(report.userId) ?: return

    val builder: EmbedBuilder.() -> Unit = {
        color = Color.RED.kColor

        field {
            name = "Report Closed"
            value = "Any response will create a new report."
        }

        author {
            name = guild.name
            icon = guild.getIconUrl(Image.Format.PNG)
        }
    }

    val mostRecentMessage = user.getDmChannel().messages.firstOrNull { it.author == kord.getSelf() }

    if (mostRecentMessage != null)
        mostRecentMessage.edit {
            builder(embed!!)
        }
    else
        user.sendPrivateMessage {
            builder(this)
        }
}