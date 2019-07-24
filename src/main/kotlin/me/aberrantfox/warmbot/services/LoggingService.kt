package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.messages.Locale
import net.dv8tion.jda.core.entities.*
import java.awt.Color

@Service
class LoggingService(private val config: Configuration, jdaInitializer: JdaInitializer) {
    init {
        config.guildConfigurations.filter { it.loggingConfiguration.loggingChannel.isValidChannel() }.forEach {
            it.loggingConfiguration.apply { if (logStartup) log(loggingChannel, Locale.messages.STARTUP_LOG) }
        }
    }

    fun memberOpen(report: Report) = getLogConfig(report.guildId).apply {
        if (logMemberOpen) log(loggingChannel, Locale.inject({ MEMBER_OPEN_LOG }, "user".pairTo(report.reportToUser())))
    }

    fun staffOpen(guild: Guild, channelName: String, staff: User) = getLogConfig(guild.id).apply {
        if (logStaffOpen) log(loggingChannel, Locale.inject({ STAFF_OPEN_LOG }, "channel" to channelName, "staff".pairTo(staff)))
    }

    fun archive(guild: Guild, channelName: String, staff: User) = getLogConfig(guild.id).apply {
        if (logArchive) log(loggingChannel, Locale.inject({ ARCHIVE_LOG }, "channel" to channelName, "staff".pairTo(staff)))
    }

    fun commandClose(guild: Guild, channelName: String, staff: User) = getLogConfig(guild.id).apply {
        if (logClose) log(loggingChannel, Locale.inject({ COMMAND_CLOSE_LOG }, "channel" to channelName, "staff".pairTo(staff)))
    }

    fun manualClose(guild: Guild, channelName: String) = getLogConfig(guild.id).apply {
        if (logClose) log(loggingChannel, Locale.inject({ MANUAL_CLOSE_LOG }, "channel" to channelName))
    }

    fun edit(report: Report, old: String, new: String) = with(getLogConfig(report.guildId)) {
        if (logEdits) logEmbed(loggingChannel, buildEditEmbed(report, old, new))
    }

    fun error(guild: Guild, message: String) = with(getLogConfig(guild.id)) {
        log(loggingChannel, Locale.inject({ ERROR_LOG }, "message" to message))
    }

    fun command(command: CommandEvent, additionalInfo: String = "") = getLogConfig(command.guild!!.id).apply {
        val author = command.author.fullName()
        val commandName = command.commandStruct.commandName
        val channelName = command.channel.name

        if (logCommands) log(loggingChannel, Locale.inject({ COMMAND_LOG },
            "author" to author, "commandName" to commandName, "channelName" to channelName, "additionalInfo" to additionalInfo))
    }

    private fun String.pairTo(user: User) = this to user.fullName()

    private fun getLogConfig(guildId: String) = config.getGuildConfig(guildId)!!.loggingConfiguration
    private fun log(logChannelId: String, message: String) = logChannelId.takeIf { it.isNotEmpty() }?.idToTextChannel()
        ?.sendMessage(message)?.queue()
    private fun logEmbed(logChannelId: String, embed: MessageEmbed) = logChannelId.idToTextChannel().sendMessage(embed).queue()

    private fun buildEditEmbed(report: Report, old: String, new: String) =
        embed {
            fun createFields(title: String, message: String) = message.chunked(1024).mapIndexed { index, chunk ->
                MessageEmbed.Field(if (index == 0) title else "(cont)", chunk, false)
            }

            val channel = report.reportToChannel()?.asMention ?: "<Failed to retrieve channel>"
            addField("Edit Detected!", "The user has performed a message edit in $channel.", false)
            createFields("Old Content", old).forEach { addField(it) }
            createFields("New Content", new).forEach { addField(it) }
            setThumbnail(report.reportToUser().effectiveAvatarUrl)
            setColor(Color.YELLOW)
        }
}