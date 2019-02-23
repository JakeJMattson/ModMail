package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.messages.Locale
import net.dv8tion.jda.core.entities.User

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

    private fun String.pairTo(user: User) = this to user.fullName()
    private fun String.pairTo(report: Report) = this.pairTo(report.userId.idToUser())

    private fun getLogConfig(guildId: String) = config.getGuildConfig(guildId)!!.loggingConfiguration
    private fun log(logChannelId: String, message: String) = logChannelId.idToTextChannel().sendMessage(message).queue()
}