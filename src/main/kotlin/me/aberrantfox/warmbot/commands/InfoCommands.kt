package me.aberrantfox.warmbot.commands

import me.aberrantfox.warmbot.extensions.archiveString
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.findReport
import me.jakejmattson.kutils.api.annotations.CommandSet
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.command.commands
import net.dv8tion.jda.api.entities.TextChannel

@CommandSet("Info")
fun infoCommands() = commands {
    command("ReportInfo") {
        description = Locale.INFO_DESCRIPTION
        execute(TextChannelArg("Channel").makeOptional { it.channel as TextChannel },
            ChoiceArg("Field", "user", "channel", "guild", "all").makeOptional("all")) {
            val (targetChannel, choice) = it.args
            val error = "Command should be invoked in a report channel or target a report channel."

            val report = targetChannel.findReport() ?: return@execute it.respond(error)

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
        description = Locale.IS_REPORT_DESCRIPTION
        execute(TextChannelArg("Channel").makeOptional { it.channel as TextChannel }) {
            val channel = it.args.first
            val isReport = channel.findReport() != null

            it.respond("${channel.asMention} ${if (isReport) "is" else "is not"} a valid report channel.")
        }
    }

    command("PeekHistory") {
        description = Locale.PEEK_HISTORY_DESCRIPTION
        execute(UserArg) {
            val user = it.args.first
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