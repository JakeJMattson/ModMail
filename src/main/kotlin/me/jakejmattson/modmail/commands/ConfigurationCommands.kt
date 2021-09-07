package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.arguments.CategoryArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.commands.commands
import me.jakejmattson.modmail.extensions.reactSuccess
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.Permission

@Suppress("unused")
fun configurationCommands(configuration: Configuration) = commands("Configuration", Permission.GUILD_OWNER) {
    slash("ReportCategory") {
        description = Locale.SET_REPORT_CATEGORY_DESCRIPTION
        execute(CategoryArg) {
            configuration[guild]!!.reportCategory = args.first.id
            configuration.save()
            reactSuccess()
        }
    }

    slash("ArchiveChannel") {
        description = Locale.SET_ARCHIVE_CHANNEL_DESCRIPTION
        execute(ChannelArg) {
            configuration[guild]!!.archiveChannel = args.first.id
            configuration.save()
            reactSuccess()
        }
    }

    slash("StaffRole") {
        description = Locale.SET_STAFF_ROLE_DESCRIPTION
        execute(RoleArg) {
            configuration[guild]!!.staffRoleId = args.first.id
            configuration.save()
            reactSuccess()
        }
    }

    slash("LoggingChannel") {
        description = Locale.SET_LOGGING_CHANNEL_DESCRIPTION
        execute(ChannelArg) {
            configuration[guild]!!.loggingConfiguration.loggingChannel = args.first.id
            configuration.save()
            reactSuccess()
        }
    }
}