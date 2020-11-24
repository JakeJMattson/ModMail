package me.jakejmattson.modmail.commands

import com.gitlab.kordlib.core.behavior.channel.*
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.modmail.arguments.ReportChannelArg
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.listeners.deletionQueue
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*

@Suppress("unused")
fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands("Report") {
    guildCommand("Close") {
        description = Locale.CLOSE_DESCRIPTION
        execute(ReportChannelArg) {
            val reportChannel = args.first

            reportChannel.report.release(discord.api)
            deletionQueue.add(reportChannel.channel.id)
            reportChannel.channel.delete()
            handleInvocation(reportChannel)
            loggingService.commandClose(guild, reportChannel.channel.name, author)
        }
    }

    guildCommand("Archive") {
        description = Locale.ARCHIVE_DESCRIPTION
        execute(ReportChannelArg, EveryArg("Info").makeOptional("")) {
            val (reportChannel, note) = args

            val (channel, report) = reportChannel
            val config = configuration[channel.guild.id]
            val archiveChannel = config?.getLiveArchiveChannel(channel.kord)

            if (archiveChannel == null) {
                respond("No archive channel available!")
                return@execute
            }

            val archiveMessage = "User ID: ${report.userId}\nAdditional Information: " +
                if (note.isNotEmpty()) note else "<None>"

            archiveChannel.createMessage {
                content = archiveMessage
                addFile("$${channel.name}.txt", channel.archiveString().toByteArray().inputStream())

                reportChannel.report.release(discord.api)
                deletionQueue.add(channel.id)
                channel.delete()
            }

            handleInvocation(reportChannel)
            loggingService.archive(guild, channel.name, author)
        }
    }

    guildCommand("Note") {
        description = Locale.NOTE_DESCRIPTION
        execute(ReportChannelArg, EveryArg("Note")) {
            val reportChannel = args.first
            val channel = reportChannel.channel
            val messageAuthor = author

            channel.createEmbed {
                author {
                    name = messageAuthor.tag
                    icon = messageAuthor.avatar.url
                }
                description = args.second
            }

            loggingService.command(this)
            handleInvocation(reportChannel)
        }
    }

    guildCommand("Tag") {
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

    guildCommand("ResetTags") {
        description = Locale.RESET_TAGS_DESCRIPTION
        execute(ReportChannelArg) {
            val reportChannel = args.first
            val (channel, report) = reportChannel
            val user = discord.api.getUser(report.userId) ?: return@execute
            val newName = user.username

            channel.edit {
                name = newName
            }

            loggingService.command(this, "Channel is now $newName")
            handleInvocation(reportChannel)
        }
    }
}