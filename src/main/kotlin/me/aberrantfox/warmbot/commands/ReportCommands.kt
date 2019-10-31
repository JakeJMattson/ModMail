package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.command.*
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
        description = Locale.CLOSE_DESCRIPTION
        execute {
            val channel = it.channel as TextChannel

            deletionQueue.add(channel.id)
            channel.delete().queue()
            loggingService.commandClose(it.guild!!, channel.name, it.author)
        }
    }

    command("Archive") {
        requiresGuild = true
        description = Locale.ARCHIVE_DESCRIPTION
        execute(SentenceArg("Additional Info").makeOptional("")) {
            val relevantGuild = configuration.getGuildConfig(it.message.guild.id)!!
            val channel = it.channel.id.idToTextChannel()!!
            val report = channel.channelToReport()
            val note = it.args.first

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
        description = Locale.NOTE_DESCRIPTION
        execute(SentenceArg) {
            val note = it.args.first

            it.respond {
                addField("Additional Information", note, false)
                color = Color.ORANGE
            }

            it.message.delete().queue()
            loggingService.command(it)
        }
    }

    command("Move") {
        requiresGuild = true
        description = Locale.MOVE_DESCRIPTION
        execute(CategoryArg, BooleanArg("Sync Permissions").makeOptional(true)) {
            val channel = it.channel as GuildChannel
            val manager = channel.manager
            val oldCategory = channel.parent
            val (newCategory, shouldSync) = it.args

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
        description = Locale.TAG_DESCRIPTION
        execute(WordArg("Word or Emote")) {
            val tag = it.args.first
            val channel = it.channel as TextChannel

            channel.manager.setName("$tag-${channel.name}").queue()
            it.message.delete().queue()
            loggingService.command(it, "Added tag :: $tag")
        }
    }

    command("ResetTags") {
        requiresGuild = true
        description = Locale.RESET_TAGS_DESCRIPTION
        execute { event ->
            val channel = event.channel as TextChannel
            val report = channel.channelToReport()

            event.discord.jda.retrieveUserById(report.userId).queue {
                val name = it.name.replace("\\s+".toRegex(), "-")
                channel.manager.setName(name).queue()
                loggingService.command(event, "Channel is now $name")
            }

            event.message.delete().queue()
        }
    }
}