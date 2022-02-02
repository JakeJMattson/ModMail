package me.jakejmattson.modmail.commands

import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.channel.edit
import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.commands.commands
import me.jakejmattson.discordkt.api.extensions.pfpUrl
import me.jakejmattson.modmail.arguments.ReportChannelArg
import me.jakejmattson.modmail.extensions.archiveString
import me.jakejmattson.modmail.extensions.handleInvocation
import me.jakejmattson.modmail.listeners.deletionQueue
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.LoggingService
import me.jakejmattson.modmail.services.release

@Suppress("unused")
fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands("Report") {
    slash("Close") {
        description = Locale.CLOSE_DESCRIPTION
        execute(ReportChannelArg) {
            val reportChannel = args.first

            reportChannel.report.release(discord.kord)
            deletionQueue.add(reportChannel.channel.id)
            reportChannel.channel.delete()
            handleInvocation(reportChannel)
            loggingService.commandClose(guild, reportChannel.channel.name, author)
        }
    }

    slash("Archive") {
        description = Locale.ARCHIVE_DESCRIPTION
        execute(ReportChannelArg, EveryArg("Info").optional("")) {
            val (reportChannel, note) = args

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

            handleInvocation(reportChannel)
            loggingService.archive(guild, channel.name, author)
        }
    }

    slash("Note") {
        description = Locale.NOTE_DESCRIPTION
        execute(ReportChannelArg, EveryArg("Note")) {
            val reportChannel = args.first
            val channel = reportChannel.channel
            val messageAuthor = author

            channel.createEmbed {
                author {
                    name = messageAuthor.tag
                    icon = messageAuthor.pfpUrl
                }
                description = args.second
            }

            loggingService.command(this)
            handleInvocation(reportChannel)
        }
    }

    slash("Tag") {
        description = Locale.TAG_DESCRIPTION
        execute(ReportChannelArg, AnyArg("Tag")) {
            val reportChannel = args.first
            val channel = reportChannel.channel
            val tag = args.second

            channel.edit {
                name = "$tag-${channel.name}"
            }

            loggingService.command(this, "Added tag :: $tag")
            handleInvocation(reportChannel)
        }
    }

    slash("ResetTags") {
        description = Locale.RESET_TAGS_DESCRIPTION
        execute(ReportChannelArg) {
            val reportChannel = args.first
            val (channel, report) = reportChannel
            val user = discord.kord.getUser(report.userId) ?: return@execute
            val newName = user.username

            channel.edit {
                name = newName
            }

            loggingService.command(this, "Channel is now $newName")
            handleInvocation(reportChannel)
        }
    }
}