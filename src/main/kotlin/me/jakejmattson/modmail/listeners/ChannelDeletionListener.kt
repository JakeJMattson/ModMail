package me.jakejmattson.modmail.listeners

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.event.channel.TextChannelDeleteEvent
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.modmail.services.*

val deletionQueue = ArrayList<Snowflake>()

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