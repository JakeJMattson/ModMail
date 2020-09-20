package me.jakejmattson.modmail.commands

import com.gitlab.kordlib.core.behavior.channel.*
import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.extensions.toSnowflake
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.listeners.deletionQueue
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*

fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands("Report") {
    command("Close") {
        description = Locale.CLOSE_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel.asChannel() as TextChannel }) {
            val inputChannel = args.first
            val channel = inputChannel.toReportChannel()?.channel
                ?: return@execute respond(createChannelError(inputChannel))

            deletionQueue.add(channel.id)
            channel.delete()
            loggingService.commandClose(guild!!, channel.name, author)
        }
    }

    command("Archive") {
        description = Locale.ARCHIVE_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel as TextChannel }, EveryArg("Info").makeOptional("")) {
            val (inputChannel, note) = args

            val (channel, report) = inputChannel.toReportChannel()
                ?: return@execute respond(createChannelError(inputChannel))

            val config = configuration[channel.guild.id.longValue]

            val archiveChannel = config?.getLiveArchiveChannel(channel.kord)
                ?: return@execute respond("No archive channel available!")

            val archiveMessage = "User ID: ${report.userId}\nAdditional Information: " +
                if (note.isNotEmpty()) note else "<None>"

            archiveChannel.createMessage {
                content = archiveMessage
                addFile("$${channel.name}.txt", channel.archiveString().toByteArray().inputStream())
                deletionQueue.add(channel.id)
                channel.delete()
            }

            loggingService.archive(guild!!, channel.name, author)
        }
    }

    command("Note") {
        description = Locale.NOTE_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeNullableOptional { it.channel as TextChannel }, EveryArg("Note")) {
            val inputChannel = args.first!!
            val channel = inputChannel.toReportChannel()?.channel
                ?: return@execute respond(createChannelError(inputChannel))
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

    command("Tag") {
        description = Locale.TAG_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel as TextChannel }, AnyArg("Tag")) {
            val inputChannel = args.first
            val channel = inputChannel.toReportChannel()?.channel
                ?: return@execute respond(createChannelError(inputChannel))

            val tag = args.second

            channel.edit {
                name = "$tag-${channel.name}"
            }

            loggingService.command(this, "Added tag :: $tag")
            message.delete()
        }
    }

    command("ResetTags") {
        description = Locale.RESET_TAGS_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel as TextChannel }) {
            val inputChannel = args.first
            val (channel, report) = inputChannel.toReportChannel()
                ?: return@execute respond(createChannelError(inputChannel))

            val user = report.userId.toSnowflake()?.let { discord.api.getUser(it) } ?: return@execute
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