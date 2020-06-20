package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.dsl.embed.embed
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.member.*

class GuildMigrationListener(val configuration: Configuration, private val guildService: GuildService) {

    @Subscribe
    fun onGuildBotJoin(event: GuildJoinEvent) = guildService.initInGuild(event.guild)

    @Subscribe
    fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val member = event.member
        val user = member.user
        val report = user.userToReport() ?: return

        if (report.guildId != event.guild.id)
            return

        report.reportToChannel()?.sendMessage(embed {
            addField("User has rejoined server!", "This report is now reactivated.", false)
            color = successColor
        })?.queue()

        if (member.isDetained())
            member.mute()
    }

    @Subscribe
    fun onGuildMemberLeave(event: GuildMemberLeaveEvent) {
        val user = event.user
        val guild = event.guild

        val report = user.userToReport() ?: return
        if (report.guildId != guild.id) return

        val message = if (guild.retrieveBanList().complete().any { it.user.id == user.id }) "was banned from" else "has left"

        report.reportToChannel()?.sendMessage(createResponse(message))?.queue()
    }

    private fun createResponse(message: String) = embed {
        addField("User no longer in server!", "This user $message the server.", false)
        color = failureColor
    }
}