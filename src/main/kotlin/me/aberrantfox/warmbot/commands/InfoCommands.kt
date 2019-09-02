package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.warmbot.extensions.archiveString
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.entities.*

@CommandSet("Info")
fun infoCommands() = commands {
    command("ReportInfo") {
        requiresGuild = true
        description = Locale.messages.INFO_DESCRIPTION
        expect(arg(TextChannelArg("Report Channel"), optional = true, default = { it.channel }),
            arg(ChoiceArg("Field", "user", "channel", "guild", "all"), optional = true, default = "all"))
        execute {
            val targetChannel = it.args.component1() as TextChannel
            val choice = it.args.component2() as String
            val error = "Command should be invoked in a report channel or target a report channel."

            if (!targetChannel.isReportChannel()) return@execute it.respond(error)

            val report = targetChannel.channelToReport()

            with(report) {
                val allData =
                    "User ID: $userId\n" +
                        "Channel ID: $channelId\n" +
                        "Guild ID: $guildId"

                it.respond(
                    when (choice) {
                        "user" -> userId
                        "channel" -> channelId
                        "guild" -> guildId
                        "all" -> allData
                        else -> "Invalid selection!"
                    }
                )
            }
        }
    }

    command("IsReport") {
        requiresGuild = true
        description = Locale.messages.IS_REPORT_DESCRIPTION
        expect(arg(TextChannelArg("Channel"), optional = true, default = { it.channel }))
        execute {
            val channel = it.args.component1() as TextChannel
            val isReport = channel.isReportChannel()

            it.respond("${channel.asMention} ${if (isReport) "is" else "is not"} a valid report channel.")
        }
    }

    command("PeekHistory") {
        requiresGuild = true
        description = Locale.messages.PEEK_HISTORY_DESCRIPTION
        expect(UserArg)
        execute {
            val user = it.args.component1() as User
            val channel = it.channel

            val privateChannel = user.openPrivateChannel().complete()
                ?: return@execute it.respond("Unable to establish private channel. Direct messages are disabled or the bot is blocked.")

            val history = privateChannel.archiveString().toByteArray()

            if (history.isEmpty())
                return@execute it.respond("No history available.")

            channel.sendFile(history, "$${user.id}.txt").queue()
        }
    }
}