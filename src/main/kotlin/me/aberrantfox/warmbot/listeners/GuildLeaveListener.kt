package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.warmbot.extensions.idToTextChannel
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent
import java.awt.Color

class GuildLeaveListener(val configuration: Configuration) {
    @Subscribe
    fun onGuildLeave(event: GuildMemberLeaveEvent) {
        val user = event.user

        if (!event.user.hasReportChannel()) return

        val reportId = user.userToReport().channelId
        val message = if (event.guild.banList.complete().any { it.user.id == user.id }) "was banned from" else "has left"

        reportId.idToTextChannel().sendMessage(createResponse(message)).queue()
    }

    private fun createResponse(message: String) = embed {
        addField("User no longer in server!", "This user $message the server.", false)
        setColor(Color.red)
    }
}