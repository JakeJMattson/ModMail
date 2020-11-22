package me.jakejmattson.modmail.listeners

import com.gitlab.kordlib.common.kColor
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.event.guild.*
import com.gitlab.kordlib.core.firstOrNull
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.discordkt.api.extensions.addField
import me.jakejmattson.modmail.services.*
import java.awt.Color

@Suppress("unused")
fun guildMigration(guildService: GuildService) = listeners {
    on<MemberLeaveEvent> {
        val report = user.toLiveReport() ?: return@on
        if (report.guild.id != guild.id) return@on

        val message = guild.bans.firstOrNull { it.user.id == user.id }?.let {
            "This user was banned for reason: ${it.reason}"
        } ?: "This user has left the server."

        report.channel.createEmbed {
            addField("User no longer in server!", message)
            color = Color.red.kColor
        }
    }

    on<MemberJoinEvent> {
        val report = member.asUser().toLiveReport() ?: return@on

        if (report.guild.id != guild.id)
            return@on

        report.channel.createEmbed {
            addField("User has rejoined server!", "This report is now reactivated.")
            color = Color.green.kColor
        }

        if (member.isDetained())
            member.mute()
    }

    on<GuildCreateEvent> {
        guildService.initInGuild(guild)
    }
}