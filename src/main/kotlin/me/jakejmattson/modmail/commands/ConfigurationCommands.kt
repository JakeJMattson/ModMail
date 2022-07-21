package me.jakejmattson.modmail.commands

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.entity.channel.Category
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.RoleArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.Configuration

@Suppress("unused")
fun configurationCommands(configuration: Configuration) = commands("Configuration", Permissions(Permission.All)) {
    slash("ReportCategory") {
        description = Locale.SET_REPORT_CATEGORY_DESCRIPTION
        execute(ChannelArg<Category>()) {
            val category = args.first
            configuration[guild]!!.reportCategory = category.id
            configuration.save()
            respond("Report category updated to ${category.mention}")
        }
    }

    slash("ArchiveChannel") {
        description = Locale.SET_ARCHIVE_CHANNEL_DESCRIPTION
        execute(ChannelArg) {
            val channel = args.first
            configuration[guild]!!.archiveChannel = channel.id
            configuration.save()
            respond("Archive channel updated to ${channel.mention}")
        }
    }

    slash("LoggingChannel") {
        description = Locale.SET_LOGGING_CHANNEL_DESCRIPTION
        execute(ChannelArg) {
            val channel = args.first
            configuration[guild]!!.loggingConfiguration.loggingChannel = channel.id
            configuration.save()
            respond("Logging channel updated to ${channel.mention}")
        }
    }

    slash("StaffRole") {
        description = Locale.SET_STAFF_ROLE_DESCRIPTION
        execute(RoleArg) {
            val role = args.first
            configuration[guild]!!.staffRoleId = role.id
            configuration.save()
            respond("Staff role updated to ${role.mention}")
        }
    }
}