package me.jakejmattson.modmail.commands

import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.channel.edit
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.pfpUrl
import me.jakejmattson.modmail.arguments.toReportChannel
import me.jakejmattson.modmail.extensions.archiveString
import me.jakejmattson.modmail.listeners.deletionQueue
import me.jakejmattson.modmail.services.*

@Suppress("unused")
fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands("Report") {
    slash("Close") {
        description = Locale.CLOSE_DESCRIPTION
        execute {
            val reportChannel = channel.toReportChannel()

            if (reportChannel == null) {
                respond("Invalid report channel")
                return@execute
            }

            reportChannel.report.release(discord.kord)
            deletionQueue.add(reportChannel.channel.id)
            reportChannel.channel.delete()

            respond("Report was closed.")
            loggingService.commandClose(guild, reportChannel.channel.name, author)
        }
    }

    slash("Archive") {
        description = Locale.ARCHIVE_DESCRIPTION
        execute(EveryArg("Info", "A message sent along side the archive file").optional("")) {
            val (note) = args
            val reportChannel = channel.toReportChannel()

            if (reportChannel == null) {
                respond("Invalid report channel")
                return@execute
            }

            val (channel, report) = reportChannel
            val config = configuration[channel.getGuild()]
            val archiveChannel = config?.getLiveArchiveChannel(channel.kord)

            if (archiveChannel == null) {
                respond("No archive channel available!")
                return@execute
            }

            val archiveMessage = "User ID: ${report.userId}\nAdditional Information: " + note.ifEmpty { "<None>" }

            archiveChannel.createMessage {
                content = archiveMessage
                addFile("$${channel.name}.txt", channel.archiveString().toByteArray().inputStream())

                reportChannel.report.release(discord.kord)
                deletionQueue.add(channel.id)
                channel.delete()
            }

            respond("Report was archived.")
            loggingService.archive(guild, channel.name, author)
        }
    }

    slash("Note") {
        description = Locale.NOTE_DESCRIPTION
        execute(EveryArg("Note", "The note content")) {
            val reportChannel = channel.toReportChannel()

            if (reportChannel == null) {
                respond("Invalid report channel")
                return@execute
            }

            val channel = reportChannel.channel
            val messageAuthor = author

            channel.createEmbed {
                author {
                    name = messageAuthor.tag
                    icon = messageAuthor.pfpUrl
                }
                description = args.first
            }

            loggingService.command(this)
            respond("Note added.")
        }
    }

    slash("Tag") {
        description = Locale.TAG_DESCRIPTION
        execute(AnyArg("Tag", "A prefix or emoji")) {
            val reportChannel = channel.toReportChannel()

            if (reportChannel == null) {
                respond("Invalid report channel")
                return@execute
            }

            val channel = reportChannel.channel
            val tag = args.first

            channel.edit {
                name = "$tag-${channel.name}"
            }

            loggingService.command(this, "Added tag :: $tag")
            respond("Tag added.")
        }
    }

    slash("ResetTags") {
        description = Locale.RESET_TAGS_DESCRIPTION
        execute {
            val reportChannel = channel.toReportChannel()

            if (reportChannel == null) {
                respond("Invalid report channel")
                return@execute
            }

            val (channel, report) = reportChannel
            val user = discord.kord.getUser(report.userId) ?: return@execute
            val newName = user.username

            channel.edit {
                name = newName
            }

            loggingService.command(this, "Channel is now $newName")
            respond("Tags reset.")
        }
    }
}