package me.jakejmattson.modmail.commands

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.entity.channel.Category
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.edit
import me.jakejmattson.modmail.services.*

@Suppress("unused")
fun configurationCommands(configuration: Configuration) = commands("Configuration", Permissions(Permission.All)) {
    slash("Configure", Locale.CONFIGURE_DESCRIPTION) {
        execute(ChannelArg<Category>("ReportCategory", "The category where new reports will be created"),
            ChannelArg("ArchiveChannel", "The channel where archived reports will be sent"),
            ChannelArg("LoggingChannel", "The channel where logging messages will be sent")) {
            val (reports, archive, logging) = args
            configuration.edit { this[guild] = GuildConfiguration("", reports.id, archive.id, LoggingConfiguration(logging.id)) }
            respond("${guild.name} configured.\n" +
                "Report Category: ${reports.mention}\n" +
                "Archive Channel: ${archive.mention}\n" +
                "Logging Channel: ${logging.mention}"
            )
        }
    }

    slash("ReportCategory", Locale.REPORT_CATEGORY_DESCRIPTION) {
        execute(ChannelArg<Category>("Category", "The category where new reports will be created")) {
            val category = args.first
            configuration.edit { this[guild]!!.reportCategory = category.id }
            respond("Report Category updated to ${category.mention}")
        }
    }

    slash("ArchiveChannel", Locale.ARCHIVE_CHANNEL_DESCRIPTION) {
        execute(ChannelArg("Channel", "The channel where archived reports will be sent")) {
            val channel = args.first
            configuration.edit { this[guild]!!.archiveChannel = channel.id }
            respond("Archive Channel updated to ${channel.mention}")
        }
    }

    slash("LoggingChannel", Locale.LOGGING_CHANNEL_DESCRIPTION) {
        execute(ChannelArg("Channel", "The channel where logging messages will be sent")) {
            val channel = args.first
            configuration.edit { this[guild]!!.loggingConfiguration.loggingChannel = channel.id }
            respond("Logging Channel updated to ${channel.mention}")
        }
    }
}