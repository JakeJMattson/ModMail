package me.jakejmattson.modmail.extensions

import me.jakejmattson.modmail.services.*
import net.dv8tion.jda.api.entities.*

data class ReportChannel(val channel: TextChannel, val report: Report)

fun MessageChannel.toReportChannel() = findReport()?.let { report ->
    val channel = this as? TextChannel

    if (channel != null)
        ReportChannel(channel, report)
    else
        null
}