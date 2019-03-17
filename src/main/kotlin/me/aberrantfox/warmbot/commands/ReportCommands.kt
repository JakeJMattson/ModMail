package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.warmbot.arguments.*
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.managers.ChannelManager
import java.awt.Color

@CommandSet("report")
fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands {
    command("Close") {
        requiresGuild = true
        description = Locale.messages.CLOSE_DESCRIPTION
        execute {
            val channel = it.channel as TextChannel

            channel.delete().queue()
            loggingService.close(it.guild!!.id, channel.name, it.author)
        }
    }

    command("Archive") {
        requiresGuild = true
        description = Locale.messages.ARCHIVE_DESCRIPTION
        expect(arg(SentenceArg("Additional Info"), optional = true, default = ""))
        execute {
            val relevantGuild = configuration.getGuildConfig(it.message.guild.id)!!
            val archiveChannel = relevantGuild.archiveChannel.idToTextChannel()
            val channel = it.channel.id.idToTextChannel()

            val note = it.args.component1() as String

            if (note.isNotEmpty())
                archiveChannel.sendMessage(note).queue()

            archiveChannel.sendFile(it.channel.archiveString(configuration.prefix).toByteArray(),
                "$${it.channel.name}.txt").queue {
                channel.delete().queue()
            }

            loggingService.archive(it.guild!!.id, channel.name, it.author)
        }
    }

    command("Note") {
        requiresGuild = true
        description = Locale.messages.NOTE_DESCRIPTION
        expect(SentenceArg)
        execute {
            val note = it.args.component1() as String

            it.respond(
                embed {
                    addField("Additional Information", note, false)
                    color(Color.ORANGE)
                }
            )

            it.message.delete().queue()
            loggingService.command(it)
        }
    }

    command("Move") {
        requiresGuild = true
        description = Locale.messages.MOVE_DESCRIPTION
        expect(arg(CategoryArg("Category ID")),
            arg(BooleanArg("Sync Permissions"), optional = true, default = true))
        execute {
            val channel = it.channel as Channel
            val manager = ChannelManager(channel)
            val oldCategory = channel.parent
            val newCategory = it.args.component1() as Category
            val shouldSync = it.args.component2() as Boolean

            if (shouldSync) {
                manager.sync(newCategory).queue { manager.setParent(newCategory).queue() }
            } else {
                manager.setParent(newCategory).queue()
            }

            it.message.delete().queue()
            loggingService.command(it, "Moved from `${oldCategory.name}` to `${newCategory.name}`")
        }
    }

    command("Tag") {
        requiresGuild = true
        description = Locale.messages.TAG_DESCRIPTION
        expect(WordArg("Word or Emote"))
        execute {
            val tag = it.args.component1() as String
            val channel = it.channel as TextChannel

            ChannelManager(channel).setName("$tag-${channel.name}").queue()
            it.message.delete().queue()
            loggingService.command(it, "Added tag :: $tag")
        }
    }

    command("ResetTags") {
        requiresGuild = true
        description = Locale.messages.RESET_TAGS_DESCRIPTION
        execute {
            val channel = it.channel as TextChannel
            val originalName = channel.channelToReport().reportToUser().name

            ChannelManager(channel).setName(originalName).queue()
            it.message.delete().queue()
            loggingService.command(it, "Channel is now $originalName")
        }
    }
}