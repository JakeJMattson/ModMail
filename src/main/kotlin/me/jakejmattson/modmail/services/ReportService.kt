package me.jakejmattson.modmail.services

import dev.kord.common.entity.MessageType
import dev.kord.common.entity.Snowflake
import dev.kord.common.kColor
import dev.kord.core.Kord
import dev.kord.core.behavior.*
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.Image
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.allowedMentions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.*
import me.jakejmattson.modmail.extensions.cleanContent
import java.awt.Color
import java.io.File
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class Report(val userId: Snowflake,
                  val channelId: Snowflake,
                  val guildId: Snowflake,
                  val messages: MutableMap<Snowflake, Snowflake> = mutableMapOf()) {

    suspend fun liveMember(kord: Kord) = kord.getGuild(guildId)?.getMemberOrNull(userId)
    suspend fun liveChannel(kord: Kord) = kord.getChannelOf<TextChannel>(channelId)
}

data class ReportChannel(val channel: TextChannel, val report: Report)

fun MessageChannel.toReportChannel() = (this as? TextChannel)?.findReport()?.let { report ->
    ReportChannel(this, report)
}

private val reports = Vector<Report>()

fun UserBehavior.findReport() = reports.firstOrNull { it.userId == id }
fun MessageChannelBehavior.findReport() = reports.firstOrNull { it.channelId == id }

@Service
class ReportService(private val config: Configuration,
                    private val loggingService: LoggingService,
                    private val discord: Discord) {
    init {
        GlobalScope.launch {
            reportsFolder.listFiles()?.forEach {
                val report = Json.decodeFromString<Report>(it.readText())
                val channel = discord.kord.getChannel(report.channelId)

                if (channel != null) reports.add(report) else it.delete()
            }
        }
    }

    suspend fun createReport(user: User, guild: Guild) {
        val member = user.asMemberOrNull(guild.id) ?: return

        if (guild.channels.count() >= 250) return

        val reportCategory = config[guild]?.getLiveReportCategory(guild.kord) ?: return

        createReportChannel(reportCategory, member, guild)
    }

    fun addReport(report: Report) {
        reports.add(report)
        writeReportToFile(report)
    }

    suspend fun receiveFromUser(message: Message) {
        val user = message.author!!
        val safeMessage = message.cleanContent(discord)

        with(user.findReport()) {
            val liveChannel = this?.liveChannel(message.kord) ?: return@with

            if (safeMessage.isEmpty()) return

            val newMessage = liveChannel.createMessage {
                content = safeMessage

                allowedMentions {
                    repliedUser = false
                }

                if (message.type is MessageType.Reply) {
                    messageReference = messages.entries.firstOrNull { it.value == message.referencedMessage!!.id }?.key
                }
            }

            messages[message.id] = newMessage.id
        }
    }

    fun writeReportToFile(report: Report) =
        File("$reportsFolder/${report.channelId.value}.json").writeText(Json.encodeToString(report))

    private suspend fun createReportChannel(category: Category, member: Member, guild: Guild) {
        val reportChannel = guild.createTextChannel(member.username) {
            parentId = category.id
        }

        member.reportOpenEmbed(reportChannel, false)

        val newReport = Report(member.id, reportChannel.id, guild.id, ConcurrentHashMap())
        addReport(newReport)

        member.sendPrivateMessage {
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
    reportsFolder.listFiles()?.firstOrNull { it.name.startsWith(report.channelId.toString()) }?.delete()
}

suspend fun Member.reportOpenEmbed(channel: TextChannel, detain: Boolean = false) = channel.createEmbed {
    addInlineField("User", mention)
    addInlineField("Name", "$username#$discriminator")
    addField("User ID", id.toString())
    thumbnail(pfpUrl)
    color = if (detain) Color.RED.kColor else Color.GREEN.kColor
    timestamp = Instant.now().toKotlinInstant()
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

    user.sendPrivateMessage {
        builder(this)
    }
}