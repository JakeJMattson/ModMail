package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent
import java.awt.Color

class GuildLeaveListener(private val reportService: ReportService, val configuration: Configuration) {
	@Subscribe
	fun onGuildLeave(event: GuildMemberLeaveEvent) {
		val user = event.user.id

        if( !(reportService.hasReportChannel(user)) ) return

        val reportId = reportService.getReportByUserId(user).channelId
        val reportChannel = reportService.jda.getTextChannelById(reportId)
        val message = if(event.guild.banList.complete().any { it.user.id == user }) "was banned from" else "has left"

        reportChannel.sendMessage(createResponse(message)).queue()
	}

    private fun createResponse(message: String) = embed {
        field {
            name = "User no longer in server!"
            value = "This user $message the server."
            inline = false
        }
        setColor(Color.red)
    }
}