package me.jakejmattson.modmail.listeners

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.channel.TextChannelDeleteEvent
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.modmail.services.LoggingService
import me.jakejmattson.modmail.services.close
import me.jakejmattson.modmail.services.findReport

val deletionQueue = ArrayList<Snowflake>()

@Suppress("unused")
fun channelDeletion(loggingService: LoggingService) = listeners {
    on<TextChannelDeleteEvent> {
        val report = channel.findReport() ?: return@on

        report.close(channel.kord)

        if (channel.id in deletionQueue) {
            deletionQueue.remove(channel.id)
            return@on
        }

        loggingService.manualClose(channel.getGuild(), channel.name)
    }
}