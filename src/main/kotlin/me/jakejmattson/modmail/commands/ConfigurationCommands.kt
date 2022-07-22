package me.jakejmattson.modmail.commands

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.entity.channel.Category
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.modmail.locale.Locale
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.GuildConfiguration
import me.jakejmattson.modmail.services.LoggingConfiguration

@Suppress("unused")
fun configurationCommands(configuration: Configuration) = commands("Configuration", Permissions(Permission.All)) {
    slash("Configure") {
        description = "Configure the bot channels and settings"
        execute(ChannelArg<Category>("ReportCategory", "The category where new reports will be created"),
                ChannelArg("ArchiveChannel", "The channel where archived reports will be sent"),
                ChannelArg("LoggingChannel", "The channel where logging messages will be sent")) {
            val (reports, archive, logging) = args
            configuration[guild] = GuildConfiguration("", reports.id, archive.id, LoggingConfiguration(logging.id))
            configuration.save()
        }
    }

    slash("ReportCategory") {
        description = Locale.SET_REPORT_CATEGORY_DESCRIPTION
        execute(ChannelArg<Category>("Category", "The category where new reports will be created")) {
            val category = args.first
            configuration[guild]!!.reportCategory = category.id
            configuration.save()
            respond("Report category updated to ${category.mention}")
        }
    }

    slash("ArchiveChannel") {
        description = Locale.SET_ARCHIVE_CHANNEL_DESCRIPTION
        execute(ChannelArg("Channel", "The channel where archived reports will be sent")) {
            val channel = args.first
            configuration[guild]!!.archiveChannel = channel.id
            configuration.save()
            respond("Archive channel updated to ${channel.mention}")
        }
    }

    slash("LoggingChannel") {
        description = Locale.SET_LOGGING_CHANNEL_DESCRIPTION
        execute(ChannelArg("Channel", "The channel where logging messages will be sent.")) {
            val channel = args.first
            configuration[guild]!!.loggingConfiguration.loggingChannel = channel.id
            configuration.save()
            respond("Logging channel updated to ${channel.mention}")
        }
    }
}