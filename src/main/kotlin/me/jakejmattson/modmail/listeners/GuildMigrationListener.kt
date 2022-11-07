package me.jakejmattson.modmail.listeners

import dev.kord.common.entity.Snowflake
import dev.kord.common.kColor
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.guild.*
import dev.kord.rest.Image
import kotlinx.coroutines.delay
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.discordkt.extensions.addField
import me.jakejmattson.discordkt.extensions.sendPrivateMessage
import me.jakejmattson.discordkt.extensions.thumbnail
import me.jakejmattson.modmail.services.*
import java.awt.Color

@Suppress("unused")
fun guildMigration(configuration: Configuration) = listeners {
    val banQueue = mutableListOf<Pair<Snowflake, Snowflake>>()

    on<BanAddEvent> {
        val report = user.findReport() ?: return@on
        if (report.guildId != guild.id) return@on

        banQueue.add(report.userId to report.guildId)

        report.liveChannel(kord)?.createEmbed {
            color = Color.red.kColor
            addField("User Banned!", "Reason: ${this@on.getBanOrNull()?.reason ?: ""}")
        }
    }

    on<MemberLeaveEvent> {
        val report = user.findReport() ?: return@on
        if (report.guildId != guild.id) return@on

        delay(500)

        if (banQueue.contains(user.id to report.guildId)) {
            banQueue.remove(report.userId to report.guildId)
            return@on
        }

        report.liveChannel(kord)?.createEmbed {
            color = Color.orange.kColor
            addField("User Left!", "This user has left the server.")
        }
    }

    on<MemberJoinEvent> {
        val report = member.asUser().findReport() ?: return@on
        if (report.guildId != guild.id) return@on

        report.liveChannel(kord)?.createEmbed {
            color = Color.green.kColor
            addField("User Joined!", "This report is now reactivated.")
        }

        if (member.isDetained())
            member.mute()
    }

    on<GuildCreateEvent> {
        if (configuration[guild] != null) return@on

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