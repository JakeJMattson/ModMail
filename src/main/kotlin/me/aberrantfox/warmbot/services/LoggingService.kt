package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.extensions.stdlib.isLong
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.messages.Locale
import net.dv8tion.jda.core.entities.User

@Service
class LoggingService(private val config: Configuration) {

	init {
	    emitReadyMessage()
	}

	private fun emitReadyMessage() {
		config.guildConfigurations.filter { it.loggingConfiguration != null }.forEach {
			val logConfig = it.loggingConfiguration!!

			if (logConfig.logStartup) log(logConfig.loggingChannel, Locale.messages.STARTUP_LOG)
		}
	}

	fun memberOpen(report: Report) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logMemberOpen)
			log(logConfig.loggingChannel, Locale.inject({MEMBER_OPEN_LOG}, "user" to getName(report)))
	}

	fun staffOpen(report: Report, staff: User) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logStaffOpen)
			log(logConfig.loggingChannel, Locale.inject({STAFF_OPEN_LOG}, "user" to getName(report), "staff" to staff.fullName()))
	}

	fun archive(report: Report, staff: User) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logArchive)
			log(logConfig.loggingChannel, Locale.inject({ARCHIVE_LOG}, "user" to getName(report), "staff" to staff.fullName()))
	}

	fun close(report: Report, staff: User) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logClose)
			log(logConfig.loggingChannel, Locale.inject({CLOSE_LOG}, "user" to getName(report), "staff" to staff.fullName()))
	}

	private fun getLogConfig(guildId: String) = config.getGuildConfig(guildId)!!.loggingConfiguration

	private fun getName(report: Report) = report.userId.idToUser().fullName()

	private fun log(logChannelId: String, message: String) {
		if (logChannelId.isLong()) {
			val channel = logChannelId.idToTextChannel() ?: return
			channel.sendMessage(message).queue()
		}
	}
}