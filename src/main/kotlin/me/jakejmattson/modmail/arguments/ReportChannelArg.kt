package me.jakejmattson.modmail.arguments

import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.runBlocking
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.discordkt.api.extensions.toSnowflakeOrNull
import me.jakejmattson.modmail.services.Report
import me.jakejmattson.modmail.services.findReport
import me.jakejmattson.modmail.services.getReports

open class ReportChannelArg(override val name: String = "Report Channel") : ArgumentType<ReportChannel> {
    companion object : ReportChannelArg()

    override val description = "A report channel"

    override suspend fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<ReportChannel> {
        val argChannel = arg.toSnowflakeOrNull()?.let {
            event.discord.kord.getChannelOf<TextChannel>(it)
        }?.toReportChannel(true)

        if (argChannel != null)
            return Success(argChannel)

        val implicitChannel = (event.channel as? TextChannel)?.toReportChannel(false)

        if (implicitChannel != null)
            return Success(implicitChannel, 0)

        return Error("Invalid Report Channel")
    }

    override suspend fun generateExamples(event: CommandEvent<*>): List<String> {
        return event.guild?.getReports()?.mapNotNull {
            runBlocking {
                it.toLiveReport(event.discord.kord)?.channel?.mention
            }
        } ?: listOf("<No Reports>")
    }
}

data class ReportChannel(val channel: TextChannel, val report: Report, val wasTargeted: Boolean)

fun TextChannel.toReportChannel(wasTargeted: Boolean) = findReport()?.let { report ->
    ReportChannel(this, report, wasTargeted)
}