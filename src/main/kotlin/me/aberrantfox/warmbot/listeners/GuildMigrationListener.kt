package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.warmbot.extensions.idToTextChannel
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.events.guild.member.*
import java.awt.Color

class GuildMigrationListener(val configuration: Configuration, private val guildService: GuildService) {

    @Subscribe
    fun onGuildBotJoin(event: GuildJoinEvent) = guildService.initOrLeave(event.guild)

    @Subscribe
    fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val member = event.member
        val user = member.user
        val isDetained = member.isDetained()

        if (isDetained)
            member.mute()

        if (user.hasReportChannel()) {
            val report = user.userToReport()

            report.reportToChannel().sendMessage(embed {
                addField("User has rejoined server!", "This report is now reactivated.", false)
                setColor(Color.green)
            }).queue()
        }
    }

    @Subscribe
    fun onGuildMemberLeave(event: GuildMemberLeaveEvent) {
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