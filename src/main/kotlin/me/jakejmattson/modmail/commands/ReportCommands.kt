package me.jakejmattson.modmail.commands

import dev.kord.common.kColor
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.channel.edit
import dev.kord.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.author
import me.jakejmattson.modmail.extensions.archiveString
import me.jakejmattson.modmail.listeners.deletionQueue
import me.jakejmattson.modmail.services.*
import java.awt.Color

@Suppress("unused")
fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands("Report") {
    slash("Close") {
        description = Locale.CLOSE_DESCRIPTION
        execute {
            val report = channel.findReport()

            if (report == null) {
                respond("This command must be run in a report channel")
                return@execute
            }

            report.release(discord.kord)
            deletionQueue.add(channel.id)
            channel.delete()

            respond("Report was closed.")
            loggingService.commandClose(guild, (channel as TextChannel).name, author)
        }
    }

    slash("Archive") {
        description = Locale.ARCHIVE_DESCRIPTION
        execute(EveryArg("Info", "A message sent along side the archive file").optional("")) {
            val note = args.first
            val channel = channel as TextChannel
            val report = channel.findReport()

            if (report == null) {
                respond("This command must be run in a report channel")
                return@execute
            }

            val config = configuration[report.guildId]
            val archiveChannel = config?.getLiveArchiveChannel(channel.kord)

            if (archiveChannel == null) {
                respond("No archive channel available!")
                return@execute
            }

            val archiveMessage = "User ID: ${report.userId}\nAdditional Information: " + note.ifEmpty { "<None>" }

            archiveChannel.createMessage {
                content = archiveMessage
                addFile("$${channel.name}.txt", channel.archiveString().toByteArray().inputStream())

                report.release(discord.kord)
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
            val report = channel.findReport()

            if (report == null) {
                respond("This command must be run in a report channel")
                return@execute
            }

            val messageAuthor = author

            channel.createEmbed {
                author(messageAuthor)
                description = args.first
                color = Color.white.kColor
            }

            loggingService.command(this)
            respond("Note added.")
        }
    }

    slash("Tag") {
        description = Locale.TAG_DESCRIPTION
        execute(AnyArg("Tag", "A prefix or emoji")) {
            val report = channel.findReport()

            if (report == null) {
                respond("This command must be run in a report channel")
                return@execute
            }

            val tag = args.first

            (channel as TextChannel).edit {
                name = "$tag-${(channel as TextChannel).name}"
            }

            loggingService.command(this, "Added tag :: $tag")
            respond("Tag added.")
        }
    }

    slash("ResetTags") {
        description = Locale.RESET_TAGS_DESCRIPTION
        execute {
            val report = channel.findReport()

            if (report == null) {
                respond("This command must be run in a report channel")
                return@execute
            }

            val user = discord.kord.getUser(report.userId) ?: return@execute
            val newName = user.username

            (channel as TextChannel).edit {
                name = newName
            }

            loggingService.command(this, "Channel is now $newName")
            respond("Tags reset.")
        }
    }
}