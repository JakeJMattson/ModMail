package me.jakejmattson.modmail.listeners

import dev.kord.common.kColor
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.rest.Image
import kotlinx.coroutines.flow.firstOrNull
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.discordkt.extensions.addField
import me.jakejmattson.discordkt.extensions.sendPrivateMessage
import me.jakejmattson.discordkt.extensions.thumbnail
import me.jakejmattson.modmail.services.isDetained
import me.jakejmattson.modmail.services.mute
import me.jakejmattson.modmail.services.toLiveReport
import java.awt.Color

@Suppress("unused")
fun guildMigration() = listeners {
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
        guild.owner.sendPrivateMessage {
            title = "Please configure ${guild.name}"
            description = "Please run the `/configure` command inside your server. You will be asked for a few things."
            guild.getIconUrl(Image.Format.PNG)?.let { thumbnail(it) }
            addField("Report Category", "Where new report channels will be created.")
            addField("Archive Channel", "Where reports can be archived as text.")
            addField("Logging Channel", "Where logging messages will be sent.")
            addField("Staff Role", "The role required to use this bot.")
        }
    }
}