package me.aberrantfox.warmbot.services

import me.aberrantfox.warmbot.messages.*
import me.jakejmattson.kutils.api.Discord
import me.jakejmattson.kutils.api.annotations.Service
import me.jakejmattson.kutils.api.dsl.command.CommandEvent
import me.jakejmattson.kutils.api.dsl.embed.embed
import me.jakejmattson.kutils.api.extensions.jda.fullName
import net.dv8tion.jda.api.entities.*

@Service
class LoggingService(private val discord: Discord, private val config: Configuration) {
    private val jda = discord.jda

    init {
        config.guildConfigurations
            .mapNotNull { it.loggingConfiguration.takeUnless { it.getLiveChannel(discord.jda) == null } }
            .forEach { loggingConfig ->
                if (loggingConfig.logStartup)
                    log(loggingConfig, Locale.STARTUP_LOG)
            }
    }

    fun memberOpen(report: Report) {
        val liveReport = report.toLiveReport(jda) ?: return
        val config = liveReport.guild.logConfig
        val message = Locale.MEMBER_OPEN_LOG inject "user".pairTo(liveReport.user)

        if (config.logMemberOpen)
            log(config, message)
    }

    fun staffOpen(guild: Guild, channelName: String, staff: User) {
        val config = guild.logConfig
        val message = Locale.STAFF_OPEN_LOG inject mapOf("channel" to channelName, "staff".pairTo(staff))

        if (config.logStaffOpen)
            log(config, message)
    }

    fun archive(guild: Guild, channelName: String, staff: User) {
        val config = guild.logConfig
        val message = Locale.ARCHIVE_LOG inject mapOf("channel" to channelName, "staff".pairTo(staff))

        if (config.logArchive)
            log(config, message)
    }

    fun commandClose(guild: Guild, channelName: String, staff: User) {
        val config = guild.logConfig
        val message = Locale.COMMAND_CLOSE_LOG inject mapOf("channel" to channelName, "staff".pairTo(staff))

        if (config.logClose)
            log(config, message)
    }

    fun manualClose(guild: Guild, channelName: String) {
        val config = guild.logConfig
        val message = Locale.MANUAL_CLOSE_LOG inject ("channel" to channelName)

        if (config.logClose)
            log(config, message)
    }

    fun edit(report: LiveReport, old: String, new: String) {
        val config = report.guild.logConfig

        if (config.logEdits)
            logEmbed(config, buildEditEmbed(report, old, new))
    }

    fun error(guild: Guild, content: String) {
        val config = guild.logConfig
        val message = Locale.ERROR_LOG inject ("message" to content)

        log(config, message)
    }

    fun command(command: CommandEvent<*>, additionalInfo: String = "") = getLogConfig(command.guild!!.id).apply {
        val author = command.author.fullName()
        val commandName = command.command!!.names.first()
        val channelName = command.channel.name

        if (logCommands) log(this, Locale.COMMAND_LOG inject
            mapOf(
                "author" to author,
                "commandName" to commandName,
                "channelName" to channelName,
                "additionalInfo" to additionalInfo
            )
        )
    }

    private fun String.pairTo(user: User?) = this to (user?.fullName() ?: "<user>")

    private val Guild.logConfig
        get() = getLogConfig(id)

    private fun getLogConfig(guildId: String) = config.getGuildConfig(guildId)!!.loggingConfiguration
    private fun log(config: LoggingConfiguration, message: String) = config.getLiveChannel(jda)?.sendMessage(message)?.queue()
    private fun logEmbed(config: LoggingConfiguration, embed: MessageEmbed) = config.getLiveChannel(jda)?.sendMessage(embed)?.queue()

    private fun buildEditEmbed(report: LiveReport, old: String, new: String) =
        embed {
            fun createFields(title: String, message: String) = message.chunked(1024).mapIndexed { index, chunk ->
                field {
                    name = if (index == 0) title else "(cont)"
                    value = chunk
                    inline = false
                }
            }

            val channel = report.channel.asMention
            addField("Edit Detected!", "The user has performed a message edit in $channel.", false)
            createFields("Old Content", old)
            createFields("New Content", new)
            thumbnail = report.user.effectiveAvatarUrl
            color = infoColor
        }
}