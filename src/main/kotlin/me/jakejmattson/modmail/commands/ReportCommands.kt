package me.jakejmattson.modmail.commands

import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.listeners.deletionQueue
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
import me.jakejmattson.kutils.api.annotations.CommandSet
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.command.commands
import me.jakejmattson.kutils.api.dsl.embed.embed
import me.jakejmattson.kutils.api.extensions.jda.fullName
import net.dv8tion.jda.api.entities.TextChannel

@CommandSet("Report")
fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands {
    command("Close") {
        description = Locale.CLOSE_DESCRIPTION
        execute(TextChannelArg("Report Channel").makeOptional { it.channel as TextChannel }) {
            val inputChannel = it.args.first
            val channel = inputChannel.toReportChannel()?.channel
                ?: return@execute it.respond(createChannelError(inputChannel))

            deletionQueue.add(channel.id)
            channel.delete().queue()
            loggingService.commandClose(it.guild!!, channel.name, it.author)
        }
    }

    command("Archive") {
        description = Locale.ARCHIVE_DESCRIPTION
        execute(TextChannelArg("Report Channel").makeOptional { it.channel as TextChannel }, EveryArg("Info").makeOptional("")) {
            val (inputChannel, note) = it.args

            val (channel, report) = inputChannel.toReportChannel()
                ?: return@execute it.respond(createChannelError(inputChannel))

            val config = configuration.getGuildConfig(channel.guild.id)

            val archiveChannel = config?.getLiveArchiveChannel(channel.jda)
                ?: return@execute it.respond("No archive channel available!")

            val archiveMessage = "User ID: ${report.userId}\nAdditional Information: " +
                if (note.isNotEmpty()) note else "<None>"

            archiveChannel.sendMessage(archiveMessage).queue()

            archiveChannel.sendFile(it.channel.archiveString().toByteArray(), "$${it.channel.name}.txt").queue {
                deletionQueue.add(channel.id)
                channel.delete().queue()
            }

            loggingService.archive(it.guild!!, channel.name, it.author)
        }
    }

    command("Note") {
        description = Locale.NOTE_DESCRIPTION
        execute(TextChannelArg("Report Channel").makeNullableOptional { it.channel as TextChannel }, EveryArg("Note")) {
            val inputChannel = it.args.first!!
            val channel = inputChannel.toReportChannel()?.channel
                ?: return@execute it.respond(createChannelError(inputChannel))

            channel.sendMessage(
                embed {
                    author {
                        name = it.author.fullName()
                        iconUrl = it.author.effectiveAvatarUrl
                    }
                    description = it.args.second
                    color = infoColor
                }
            ).queue()

            it.message.delete().queue()
            loggingService.command(it)
        }
    }

    command("Tag") {
        description = Locale.TAG_DESCRIPTION
        execute(TextChannelArg("Report Channel").makeOptional { it.channel as TextChannel }, AnyArg("Tag")) {
            val inputChannel = it.args.first
            val channel = inputChannel.toReportChannel()?.channel
                ?: return@execute it.respond(createChannelError(inputChannel))

            val tag = it.args.second

            channel.manager.setName("$tag-${channel.name}").queue()
            it.message.delete().queue()
            loggingService.command(it, "Added tag :: $tag")
        }
    }

    command("ResetTags") {
        description = Locale.RESET_TAGS_DESCRIPTION
        execute(TextChannelArg("Report Channel").makeOptional { it.channel as TextChannel }) { event ->
            val inputChannel = event.args.first
            val (channel, report) = inputChannel.toReportChannel()
                ?: return@execute event.respond(createChannelError(inputChannel))

            event.discord.jda.retrieveUserById(report.userId).queue {
                val name = it.name.replace("\\s+".toRegex(), "-")
                channel.manager.setName(name).queue()
                loggingService.command(event, "Channel is now $name")
            }

            event.message.delete().queue()
        }
    }
}

fun createChannelError(channel: TextChannel) = "Invalid report channel: ${channel.asMention}"