package me.jakejmattson.modmail.listeners

import com.google.common.eventbus.Subscribe
import me.jakejmattson.discordkt.api.dsl.embed.embed
import me.jakejmattson.modmail.services.*
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.member.*

class GuildMigrationListener(val configuration: Configuration, private val guildService: GuildService) {

    @Subscribe
    fun onGuildBotJoin(event: GuildJoinEvent) = guildService.initInGuild(event.guild)

    @Subscribe
    fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val member = event.member
        val user = member.user
        val report = user.toLiveReport() ?: return

        if (report.guild.id != event.guild.id)
            return

        report.channel.sendMessage(embed {
            addField("User has rejoined server!", "This report is now reactivated.", false)
            color = successColor
        }).queue()

        if (member.isDetained())
            member.mute()
    }

    @Subscribe
    fun onGuildMemberLeave(event: GuildMemberRemoveEvent) {
        val user = event.user
        val guild = event.guild

        val report = user.toLiveReport() ?: return
        if (report.guild.id != guild.id) return

        val message = guild.retrieveBanList().complete().firstOrNull { it.user.id == user.id }?.let {
            "This user was banned for reason: ${it.reason}"
        } ?: "This user has left the server."

        report.channel.sendMessage(createResponse(message)).queue()
    }

    private fun createResponse(message: String) = embed {
        addField("User no longer in server!", message)
        color = failureColor
    }
}