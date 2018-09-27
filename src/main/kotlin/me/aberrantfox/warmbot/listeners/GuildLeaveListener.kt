package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.guild.GuildBanEvent
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent
import java.awt.Color

class GuildLeaveListener(private val reportService: ReportService, val configuration: Configuration) {

	@Subscribe
	fun onGuildLeave(event: GuildMemberLeaveEvent) {

		val user = event.user.id

		if (reportService.hasReportChannel(user)) {

			val reportId = reportService.getReportByUserId(user).channelId
			val reportChannel = reportService.jda.getTextChannelById(reportId)
			val message = if(event.guild.banList.complete().any { b -> b.user.id == user }) "was banned from" else "has left"
			val embed = EmbedBuilder().addField("User no longer in server!", "This user $message the server.", false)
			embed.setColor(Color.red)

			reportChannel.sendMessage(embed.build()).queue()
		}
	}
}