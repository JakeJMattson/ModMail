package me.jakejmattson.modmail.services

import dev.kord.common.entity.Snowflake
import dev.kord.common.kColor
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.createTextChannel
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.Image
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.embed
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.util.*
import me.jakejmattson.modmail.extensions.addFailReaction
import me.jakejmattson.modmail.extensions.fullContent
import java.awt.Color
import java.io.File
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class Report(
    val userId: Snowflake,
    val channelId: Snowflake,
    val guildId: Snowflake,
    val messages: MutableMap<Snowflake, Snowflake> = mutableMapOf()
) {

    suspend fun liveMember(kord: Kord) = kord.getGuildOrNull(guildId)?.getMemberOrNull(userId)
    suspend fun liveChannel(kord: Kord) = kord.getChannelOf<TextChannel>(channelId)
}

private val reports = Vector<Report>()

fun UserBehavior.findReport() = reports.firstOrNull { it.userId == id }
fun MessageChannelBehavior.findReport() = reports.firstOrNull { it.channelId == id }

@Service
class ReportService(
    private val config: Configuration,
    private val loggingService: LoggingService,
    private val discord: Discord
) {
    init {
        GlobalScope.launch {
            reportsFolder.listFiles()?.forEach {
                val report = Json.decodeFromString<Report>(it.readText())
                val channel = report.liveChannel(discord.kord)

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
        val report = user.findReport() ?: return
        val liveChannel = report.liveChannel(message.kord) ?: return

        if (report.liveMember(message.kord) == null) {
            message.addFailReaction()
            return
        }

        val newMessage = liveChannel.createMessage {
            allowedMentions { }
            content = message.fullContent().takeIf { it.isNotBlank() } ?: "[STICKER:${message.stickers.first().name}]"
        }

        val snowflakeRegex = Regex("^\\d{17,21}$")
        val snowflakes = message.content.split(Regex("\\s+")).filter { it.matches(snowflakeRegex) }.toSet()
        val messageRegex = "^https://discord\\.com/channels/(\\d{17,21})/(\\d{17,21})/(\\d{17,21})$".toRegex()
        val messageLinks = message.content.split(Regex("\\s+")).filter { it.matches(messageRegex) }

        if (messageLinks.isNotEmpty()) {
            newMessage.replySilently {
                embed {
                    color = Color.white.kColor

                    messageLinks.map { it.unwrapMessageLink()!! }.forEach { (_, channelId, messageId) ->
                        val linkedMessage =
                            liveChannel.guild.getChannelOf<GuildMessageChannel>(channelId).getMessage(messageId)
                        addField(
                            linkedMessage.author?.fullName ?: "Unknown Author",
                            "[[$messageId]](${linkedMessage.jumpLink()})\n${linkedMessage.fullContent()}"
                        )
                    }
                }
            }
        } else if (snowflakes.isNotEmpty()) {
            newMessage.replySilently {
                content = "Potential users:\n" + snowflakes.joinToString("\n") { "`$it` = <@!$it>" }
            }
        }

        report.messages[message.id] = newMessage.id
    }

    fun writeReportToFile(report: Report) =
        File("$reportsFolder/${report.channelId.value}.json").writeText(Json.encodeToString(report))

    private suspend fun createReportChannel(category: Category, member: Member, guild: Guild) {
        val reportChannel = guild.createTextChannel(member.username) {
            parentId = category.id
        }

        member.reportOpenEmbed(reportChannel)

        val newReport = Report(member.id, reportChannel.id, guild.id, ConcurrentHashMap())
        addReport(newReport)

        member.sendPrivateMessage {
            color = Color.GREEN.kColor

            field {
                name = "Report Opened"
                value = "Someone will respond shortly, please be patient."
            }

            author {
                name = guild.name
                icon = guild.icon?.cdnUrl?.toUrl()
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

suspend fun Member.reportOpenEmbed(channel: TextChannel, opener: User? = null, detain: Boolean = false) =
    channel.createEmbed {
        addInlineField("User", mention)
        addInlineField("Name", fullName)
        addField("User ID", id.toString())
        thumbnail(pfpUrl)
        if (opener != null) footer("Opened by ${opener.fullName}", opener.pfpUrl)
        color = if (detain) Color.RED.kColor else Color.GREEN.kColor
        timestamp = Instant.now().toKotlinInstant()
    }

private suspend fun sendReportClosedEmbed(report: Report, kord: Kord) {
    val guild = kord.getGuildOrNull(report.guildId) ?: return
    val user = kord.getUser(report.userId) ?: return

    user.sendPrivateMessage {
        color = Color.RED.kColor

        field {
            name = "Report Closed"
            value = "Any response will create a new report."
        }

        author {
            name = guild.name
            icon = guild.icon?.cdnUrl?.toUrl()
        }
    }
}