package me.jakejmattson.modmail.commands

import com.gitlab.kordlib.core.behavior.channel.*
import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.extensions.toSnowflakeOrNull
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.listeners.deletionQueue
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*

fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands("Report") {
    guildCommand("Close") {
        description = Locale.CLOSE_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel.asChannel() as TextChannel }) {
            val inputChannel = args.first
            val channel = inputChannel.toReportChannel()?.channel

            if (channel == null) {
                respond(createChannelError(inputChannel))
                return@execute
            }

            deletionQueue.add(channel.id)
            channel.delete()
            loggingService.commandClose(guild, channel.name, author)
        }
    }

    guildCommand("Archive") {
        description = Locale.ARCHIVE_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel as TextChannel }, EveryArg("Info").makeOptional("")) {
            val (inputChannel, note) = args

            val reportChannel = inputChannel.toReportChannel()

            if (reportChannel == null) {
                respond(createChannelError(inputChannel))
                return@execute
            }

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
                deletionQueue.add(channel.id)
                channel.delete()
            }

            loggingService.archive(guild, channel.name, author)
        }
    }

    guildCommand("Note") {
        description = Locale.NOTE_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeNullableOptional { it.channel as TextChannel }, EveryArg("Note")) {
            val inputChannel = args.first!!
            val channel = inputChannel.toReportChannel()?.channel
            val messageAuthor = author

            if (channel == null) {
                respond(createChannelError(inputChannel))
                return@execute
            }

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
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel as TextChannel }, AnyArg("Tag")) {
            val inputChannel = args.first
            val channel = inputChannel.toReportChannel()?.channel

            if (channel == null) {
                respond(createChannelError(inputChannel))
                return@execute
            }

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
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel as TextChannel }) {
            val inputChannel = args.first

            val reportChannel = inputChannel.toReportChannel()

            if (reportChannel == null) {
                respond(createChannelError(inputChannel))
                return@execute
            }

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

fun createChannelError(channel: TextChannel) = "Invalid report channel: ${channel.mention}"