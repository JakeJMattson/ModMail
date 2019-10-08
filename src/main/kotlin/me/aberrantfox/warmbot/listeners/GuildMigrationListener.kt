package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.member.*
import java.awt.Color

class GuildMigrationListener(val configuration: Configuration, private val guildService: GuildService) {

    @Subscribe
    fun onGuildBotJoin(event: GuildJoinEvent) = guildService.initOrLeave(event.guild)

    @Subscribe
    fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val member = event.member
        val user = member.user

        if (user.hasReportChannel()) {
            val report = user.userToReport() ?: return

            if (report.guildId != event.guild.id)
                return

            report.reportToChannel()?.sendMessage(embed {
                addField("User has rejoined server!", "This report is now reactivated.", false)
                color = Color.green
            })?.queue()

            if (member.isDetained())
                member.mute()
        }
    }

    @Subscribe
    fun onGuildMemberLeave(event: GuildMemberLeaveEvent) {
        val user = event.user
        val guild = event.guild

        if (!user.hasReportChannel()) return
        val report = user.userToReport() ?: return
        if (report.guildId != guild.id) return

        val message = if (guild.retrieveBanList().complete().any { it.user.id == user.id }) "was banned from" else "has left"

        report.reportToChannel()?.sendMessage(createResponse(message))?.queue()
    }

    private fun createResponse(message: String) = embed {
        addField("User no longer in server!", "This user $message the server.", false)
        color = Color.red
    }
}