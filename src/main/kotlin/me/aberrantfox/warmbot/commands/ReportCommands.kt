package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.listeners.deletionQueue
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.entities.*
import java.awt.Color

@CommandSet("Report")
fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands {
    command("Close") {
        requiresGuild = true
        description = Locale.messages.CLOSE_DESCRIPTION
        execute {
            val channel = it.channel as TextChannel

            deletionQueue.add(channel.id)
            channel.delete().queue()
            loggingService.commandClose(it.guild!!, channel.name, it.author)
        }
    }

    command("Archive") {
        requiresGuild = true
        description = Locale.messages.ARCHIVE_DESCRIPTION
        expect(arg(SentenceArg("Additional Info"), optional = true, default = ""))
        execute {
            val relevantGuild = configuration.getGuildConfig(it.message.guild.id)!!
            val channel = it.channel.id.idToTextChannel()!!
            val report = channel.channelToReport()
            val note = it.args.component1() as String

            val archiveChannel = relevantGuild.archiveChannel.idToTextChannel()
                ?: return@execute it.respond("No archive channel set!")

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
        requiresGuild = true
        description = Locale.messages.NOTE_DESCRIPTION
        expect(SentenceArg)
        execute {
            val note = it.args.component1() as String

            it.respond(
                embed {
                    addField("Additional Information", note, false)
                    color = Color.ORANGE
                }
            )

            it.message.delete().queue()
            loggingService.command(it)
        }
    }

    command("Move") {
        requiresGuild = true
        description = Locale.messages.MOVE_DESCRIPTION
        expect(arg(CategoryArg), arg(BooleanArg("Sync Permissions"), optional = true, default = true))
        execute {
            val channel = it.channel as GuildChannel
            val manager = channel.manager
            val oldCategory = channel.parent
            val newCategory = it.args.component1() as Category
            val shouldSync = it.args.component2() as Boolean

            if (shouldSync) {
                manager.sync().queue { manager.setParent(newCategory).queue() }
            } else {
                manager.setParent(newCategory).queue()
            }

            it.message.delete().queue()
            val movement = "Moved from `${oldCategory?.name}` to `${newCategory.name}`."
            val synced = "This channel was${if(!shouldSync) " not " else " "}synced with the new category."
            loggingService.command(it, "$movement $synced")
        }
    }

    command("Tag") {
        requiresGuild = true
        description = Locale.messages.TAG_DESCRIPTION
        expect(WordArg("Word or Emote"))
        execute {
            val tag = it.args.component1() as String
            val channel = it.channel as TextChannel

            channel.manager.setName("$tag-${channel.name}").queue()
            it.message.delete().queue()
            loggingService.command(it, "Added tag :: $tag")
        }
    }

    command("ResetTags") {
        requiresGuild = true
        description = Locale.messages.RESET_TAGS_DESCRIPTION
        execute {
            val channel = it.channel as TextChannel
            val report = channel.channelToReport()
            val name = report.reportToUser()?.name ?: report.userId

            channel.manager.setName(name).queue()
            it.message.delete().queue()
            loggingService.command(it, "Channel is now $name")
        }
    }
}