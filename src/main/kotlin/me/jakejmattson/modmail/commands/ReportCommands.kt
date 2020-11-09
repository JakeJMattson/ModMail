package me.jakejmattson.modmail.commands

import com.gitlab.kordlib.core.behavior.channel.*
import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.extensions.toSnowflakeOrNull
import me.jakejmattson.modmail.arguments.ReportChannelArg
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.listeners.deletionQueue
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*

fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands("Report") {
    guildCommand("Close") {
        description = Locale.CLOSE_DESCRIPTION
        execute(ReportChannelArg) {
            val reportChannel = args.first

            reportChannel.report.release(discord.api)
            deletionQueue.add(reportChannel.channel.id)
            reportChannel.channel.delete()
            loggingService.commandClose(guild, reportChannel.channel.name, author)
        }
    }

    guildCommand("Archive") {
        description = Locale.ARCHIVE_DESCRIPTION
        execute(ReportChannelArg, EveryArg("Info").makeOptional("")) {
            val (reportChannel, note) = args

            val (channel, report) = reportChannel
            val config = configuration[channel.guild.id.longValue]
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

            loggingService.archive(guild, channel.name, author)
        }
    }

    guildCommand("Note") {
        description = Locale.NOTE_DESCRIPTION
        execute(ReportChannelArg, EveryArg("Note")) {
            val channel = args.first.channel
            val messageAuthor = author

            channel.createEmbed {
                author {
                    name = messageAuthor.tag
                    icon = messageAuthor.avatar.url
                }
                description = args.second
            }

            message.delete()
            loggingService.command(this)
        }
    }

    guildCommand("Tag") {
        description = Locale.TAG_DESCRIPTION
        execute(ReportChannelArg, AnyArg("Tag")) {
            val channel = args.first.channel
            val tag = args.second

            channel.edit {
                name = "$tag-${channel.name}"
            }

            loggingService.command(this, "Added tag :: $tag")
            message.delete()
        }
    }

    guildCommand("ResetTags") {
        description = Locale.RESET_TAGS_DESCRIPTION
        execute(ReportChannelArg) {
            val reportChannel = args.first
            val (channel, report) = reportChannel
            val user = report.userId.toSnowflakeOrNull()?.let { discord.api.getUser(it) } ?: return@execute
            val newName = user.username

            channel.edit {
                name = newName
            }

            loggingService.command(this, "Channel is now $newName")
            message.delete()
        }
    }
}