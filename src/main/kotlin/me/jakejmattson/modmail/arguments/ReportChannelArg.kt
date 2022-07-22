package me.jakejmattson.modmail.arguments

import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.runBlocking
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.DiscordContext
import me.jakejmattson.modmail.services.Report
import me.jakejmattson.modmail.services.findReport
import me.jakejmattson.modmail.services.getReports

open class ReportChannelArg(override val name: String = "ReportChannel") : ChannelArgument<ReportChannel> {
    companion object : ReportChannelArg()

    override val description = "A report channel"

    override suspend fun parse(args: MutableList<String>, discord: Discord): Channel? {
        return super.parse(args, discord)
    }

    override suspend fun transform(input: Channel, context: DiscordContext): Result<ReportChannel> {
        val reportChannel = context.discord.kord.getChannelOf<TextChannel>(input.id)?.toReportChannel(true)

        return if (reportChannel != null)
            Success(reportChannel)
        else
            Error("Invalid Report Channel")
    }

    override suspend fun generateExamples(context: DiscordContext): List<String> {
        return context.guild?.getReports()?.mapNotNull {
            runBlocking {
                it.toLiveReport(context.discord.kord)?.channel?.mention
            }
        } ?: listOf("<No Reports>")
    }
}

data class ReportChannel(val channel: TextChannel, val report: Report, val wasTargeted: Boolean)

fun TextChannel.toReportChannel(wasTargeted: Boolean) = findReport()?.let { report ->
    ReportChannel(this, report, wasTargeted)
}