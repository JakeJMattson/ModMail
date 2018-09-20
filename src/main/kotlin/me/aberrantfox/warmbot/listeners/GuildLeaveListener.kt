package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.guild.GuildBanEvent
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent
import java.awt.Color

class GuildLeaveListener(private val reportService: ReportService, val configuration: Configuration) {

	private var wasBanned = false

	@Subscribe
	fun onGuildLeave(event: GuildMemberLeaveEvent) {

		//Ignore leave event if ban was reported
		if (wasBanned) {
			wasBanned = false
			return
		}

		val user = event.user.id
		val message = "has left"

		notifyReportChannel(user, message)
	}

	@Subscribe
	fun onGuildBan(event: GuildBanEvent) {

		wasBanned = true

		val user = event.user.id
		val message = "was banned from"

		notifyReportChannel(user, message)
	}

	private fun notifyReportChannel(user: String, message: String)
	{
		if (reportService.hasReportChannel(user)) {

			val reportId = reportService.getReportByUserId(user).channelId
			val reportChannel = reportService.jda.getTextChannelById(reportId)
			val embed = EmbedBuilder().addField("User no longer in server!", "This user $message the server.", false)
			embed.setColor(Color.red)

			reportChannel.sendMessage(embed.build()).queue()
		}
	}
}