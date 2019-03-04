package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
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
        if (logMemberOpen) log(loggingChannel, Locale.inject({ MEMBER_OPEN_LOG }, "user".pairTo(report)))
    }

    fun staffOpen(report: Report, staff: User) = getLogConfig(report.guildId).apply {
        if (logStaffOpen) log(loggingChannel, Locale.inject({ STAFF_OPEN_LOG }, "user".pairTo(report), "staff".pairTo(staff)))
    }

    fun archive(report: Report, staff: User) = getLogConfig(report.guildId).apply {
        if (logArchive) log(loggingChannel, Locale.inject({ ARCHIVE_LOG }, "user".pairTo(report), "staff".pairTo(staff)))
    }

    fun close(report: Report, staff: User) = getLogConfig(report.guildId).apply {
        if (logClose) log(loggingChannel, Locale.inject({ CLOSE_LOG }, "user".pairTo(report), "staff".pairTo(staff)))
    }

    fun edit(report: Report, old: String, new: String) = with(getLogConfig(report.guildId)) {
        if (logEdits) logEmbed(loggingChannel, buildEditEmbed(report, old, new))
    }

    private fun String.pairTo(user: User) = this to user.fullName()
    private fun String.pairTo(report: Report) = this.pairTo(report.userId.idToUser())

    private fun getLogConfig(guildId: String) = config.getGuildConfig(guildId)!!.loggingConfiguration
    private fun log(logChannelId: String, message: String) = logChannelId.idToTextChannel().sendMessage(message).queue()
    private fun logEmbed(logChannelId: String, embed: MessageEmbed) = logChannelId.idToTextChannel().sendMessage(embed).queue()

    private fun buildEditEmbed(report: Report, old: String, new: String) =
        embed {
            fun createFields(title: String, message: String) = message.chunked(1024).mapIndexed { index, chunk ->
                MessageEmbed.Field(if (index == 0) title else "(cont)", chunk, false)
            }

            val channel = report.channelId.idToTextChannel().asMention
            addField("Edit Detected!", "The user has performed a message edit in $channel.", false)
            createFields("Old Content", old).forEach { addField(it) }
            createFields("New Content", new).forEach { addField(it) }
            setColor(Color.YELLOW)
        }
}