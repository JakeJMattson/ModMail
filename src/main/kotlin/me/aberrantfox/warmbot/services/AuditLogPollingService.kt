package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.warmbot.extensions.guilds
import net.dv8tion.jda.core.audit.*
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.requests.restaction.pagination.AuditLogPaginationAction
import kotlin.concurrent.fixedRateTimer

@Service
class AuditLogPollingService(private val loggingService: LoggingService) {

    private val logMap = HashMap<Guild, AuditLogPaginationAction>()
    private val channels = ArrayList<TextChannel>()

    init {
        guilds().forEach { logMap[it] = it.auditLogs }
        startListener()
    }

    private fun startListener() {
        fixedRateTimer(period = 10000) {
            val groupedChannels = channels.groupBy { it.guild }

            if (groupedChannels.isEmpty()) return@fixedRateTimer

            groupedChannels.keys.forEach { guild ->
                val oldLogs = logMap[guild]
                val newLogs = guild.auditLogs

                if (newLogs == oldLogs) return@forEach

                val channelsInThisGuild = groupedChannels.getValue(guild)

                if (channelsInThisGuild.isEmpty()) return@forEach

                val newEntries = newLogs - oldLogs!!
                val channelDeletions = newEntries.filter { it.type == ActionType.CHANNEL_DELETE }

                if (channelDeletions.isEmpty()) return@forEach

                channelsInThisGuild.forEach forEachInner@ { channel ->

                    //Find an audit log entry where the deleted channel name equals one of the registered channel names
                    val entry = channelDeletions.firstOrNull {
                        it.getChangeByKey(AuditLogKey.CHANNEL_NAME)!!.getOldValue<String>() == channel.name
                    } ?: return@forEachInner

                    loggingService.close(guild.id, channel.name, entry.user!!)
                    channels.remove(channel)
                }

                logMap[guild] = newLogs
            }
        }
    }

    fun registerChannel(channel: TextChannel) = channels.add(channel).also {
        if (!logMap.containsKey(channel.guild))
            logMap[channel.guild] = channel.guild.auditLogs
    }
}