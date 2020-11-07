package me.jakejmattson.modmail.extensions

import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.jakejmattson.modmail.services.*

data class ReportChannel(val channel: TextChannel, val report: Report)

fun TextChannel.toReportChannel() = findReport()?.let { report ->
    val channel = this as? TextChannel

    if (channel != null)
        ReportChannel(channel, report)
    else
        null
}