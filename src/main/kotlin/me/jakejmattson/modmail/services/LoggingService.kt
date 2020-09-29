package me.jakejmattson.modmail.services

import com.gitlab.kordlib.core.behavior.channel.createMessage
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.rest.builder.message.EmbedBuilder
import kotlinx.coroutines.*
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.discordkt.api.extensions.addField
import me.jakejmattson.modmail.messages.*

@Service
class LoggingService(private val discord: Discord, private val config: Configuration) {
    private val api = discord.api

    init {
        GlobalScope.launch {
            config.guildConfigurations.values
                .mapNotNull { it.loggingConfiguration.takeUnless { it.getLiveChannel(api) == null } }
                .forEach { log(it, Locale.STARTUP_LOG) }
        }
    }

    suspend fun memberOpen(report: Report) {
        val liveReport = report.toLiveReport(api) ?: return
        val config = liveReport.guild.logConfig
        val message = Locale.MEMBER_OPEN_LOG inject "user".pairTo(liveReport.user)

        if (config.logOpen)
            log(config, message)
    }

    suspend fun staffOpen(guild: Guild, channelName: String, staff: User) {
        val config = guild.logConfig
        val message = Locale.STAFF_OPEN_LOG inject mapOf("channel" to channelName, "staff".pairTo(staff))

        if (config.logOpen)
            log(config, message)
    }

    suspend fun archive(guild: Guild, channelName: String, staff: User) {
        val config = guild.logConfig
        val message = Locale.ARCHIVE_LOG inject mapOf("channel" to channelName, "staff".pairTo(staff))

        if (config.logClose)
            log(config, message)
    }

    suspend fun commandClose(guild: Guild, channelName: String, staff: User) {
        val config = guild.logConfig
        val message = Locale.COMMAND_CLOSE_LOG inject mapOf("channel" to channelName, "staff".pairTo(staff))

        if (config.logClose)
            log(config, message)
    }

    suspend fun manualClose(guild: Guild, channelName: String) {
        val config = guild.logConfig
        val message = Locale.MANUAL_CLOSE_LOG inject ("channel" to channelName)

        if (config.logClose)
            log(config, message)
    }

    suspend fun edit(report: LiveReport, old: String, new: String) {
        val config = report.guild.logConfig

        if (config.logEdits)
            logEmbed(config, buildEditEmbed(report, old, new))
    }

    suspend fun error(guild: Guild, content: String) {
        val config = guild.logConfig
        val message = Locale.ERROR_LOG inject ("message" to content)

        log(config, message)
    }

    suspend fun command(command: CommandEvent, additionalInfo: String = "") = command.guild!!.logConfig.apply {
        val author = command.author.tag
        val commandName = command.command!!.names.first()
        val channelName = (command.channel as TextChannel).name

        if (logCommands) log(this, Locale.COMMAND_LOG inject
            mapOf(
                "author" to author,
                "commandName" to commandName,
                "channelName" to channelName,
                "additionalInfo" to additionalInfo
            )
        )
    }

    private fun String.pairTo(user: User?) = this to (user?.tag ?: "<user>")

    private val Guild.logConfig
        get() = config[id.longValue]!!.loggingConfiguration

    private suspend fun log(config: LoggingConfiguration, message: String) = config.getLiveChannel(api)?.createMessage(message)
    private suspend fun logEmbed(config: LoggingConfiguration, embed: EmbedBuilder) = config.getLiveChannel(api)?.createMessage {
        this.embed = embed
    }

    private fun buildEditEmbed(report: LiveReport, old: String, new: String) = EmbedBuilder().apply {
        fun createFields(title: String, message: String) = message.chunked(1024).mapIndexed { index, chunk ->
            field {
                name = if (index == 0) title else "(cont)"
                value = chunk
                inline = false
            }
        }

        val channel = report.channel.mention

        addField("Edit Detected!", "The user has performed a message edit in $channel.")

        createFields("Old Content", old)
        createFields("New Content", new)

        thumbnail {
            report.user.avatar.url
        }
    }
}