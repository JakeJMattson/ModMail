package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.extensions.stdlib.*
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.User

class LoggingService(val jda: JDA, private val config: Configuration) {
	private val startupFormat = "Bot successfully initialized!"
	private val memberOpenFormat = "New report opened by %s"
	private val staffOpenFormat = "Report (%s) opened by %s"
	private val archiveFormat = "Report (%s) archived by %s"
	private val closeFormat = "Report (%s) closed by %s"

	fun emitReadyMessage() {
		config.guildConfigurations.filter { it.loggingConfiguration != null }.forEach {
			val logConfig = it.loggingConfiguration!!

			if (logConfig.logStartup)
				log(logConfig.loggingChannel, startupFormat)
		}
	}

	fun memberOpen(report: Report) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logMemberOpen)
			log(logConfig.loggingChannel, memberOpenFormat.format(getName(report)))
	}

	fun staffOpen(report: Report, staff: User) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logStaffOpen)
			log(logConfig.loggingChannel, staffOpenFormat.format(getName(report), staff.fullName()))
	}

	fun archive(report: Report, staff: User) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logArchive)
			log(logConfig.loggingChannel, archiveFormat.format(getName(report), staff.fullName()))
	}

	fun close(report: Report, staff: User) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logClose)
			log(logConfig.loggingChannel, closeFormat.format(getName(report), staff.fullName()))
	}

	private fun getLogConfig(guildId: String)
			= config.guildConfigurations.first { guildId == it.guildId }.loggingConfiguration

	private fun getName(report: Report) = report.userId.idToUser(jda).fullName()

	private fun log(logChannelId: String, message: String) {
		if (logChannelId.isLong()) {
			val channel = jda.getTextChannelById(logChannelId) ?: return
			channel.sendMessage(message).queue()
		}
	}
}