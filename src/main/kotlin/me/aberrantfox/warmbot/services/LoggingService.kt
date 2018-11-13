package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.extensions.jda.descriptor
import me.aberrantfox.kjdautils.extensions.stdlib.idToUser
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.User

class LoggingService(val jda: JDA, private val config: Configuration) {

	private val startupFormat = "Bot successfully initialized!"
	private val memberOpenFormat = "New report opened by %s"
	private val archiveFormat = "Report (%s) archived by %s"
	private val closeFormat = "Report (%s) closed by %s"

	fun logStartup() {
		config.guildConfigurations.filter { it.loggingConfiguration != null }.forEach {
			val logConfig = it.loggingConfiguration!!

			if (logConfig.logStartup)
				log(logConfig.loggingChannel, startupFormat)
		}
	}

	fun logMemberOpen(report: Report) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logMemberOpen)
			log(logConfig.loggingChannel, memberOpenFormat.format(getDescriptor(report)))
	}

	fun logArchive(report: Report, staff: User) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logArchive)
			log(logConfig.loggingChannel, archiveFormat.format(getDescriptor(report), staff.descriptor()))
	}

	fun logClose(report: Report, staff: User) {
		val logConfig = getLogConfig(report.guildId) ?: return

		if (logConfig.logClose)
			log(logConfig.loggingChannel, closeFormat.format(getDescriptor(report), staff.descriptor()))
	}

	private fun getLogConfig(guildId: String)
			= config.guildConfigurations.first { guildId == it.guildId }.loggingConfiguration

	private fun getDescriptor(report: Report) = report.user.idToUser(jda).descriptor()

	private fun log(logChannelId: String, message: String)= jda.getTextChannelById(logChannelId).sendMessage(message).queue()
}