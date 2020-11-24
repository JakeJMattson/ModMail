package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*

@Suppress("unused")
fun configurationCommands(configuration: Configuration) = commands("Configuration") {
    requiredPermissionLevel = Permission.GUILD_OWNER

    guildCommand("SetReportCategory") {
        description = Locale.SET_REPORT_CATEGORY_DESCRIPTION
        execute(CategoryArg) {
            val reportCategory = args.first

            configuration[reportCategory.guild.id]!!.reportCategory = reportCategory.id
            configuration.save()
            reactSuccess()
        }
    }

    guildCommand("SetArchiveChannel") {
        description = Locale.SET_ARCHIVE_CHANNEL_DESCRIPTION
        execute(ChannelArg) {
            val archiveChannel = args.first

            configuration[archiveChannel.guild.id]!!.archiveChannel = archiveChannel.id
            configuration.save()
            reactSuccess()
        }
    }

    guildCommand("SetStaffRole") {
        description = Locale.SET_STAFF_ROLE_DESCRIPTION
        execute(RoleArg) {
            val staffRole = args.first

            configuration[message.getGuild().id]!!.staffRoleId = staffRole.id
            configuration.save()
            reactSuccess()
        }
    }

    guildCommand("SetLoggingChannel") {
        description = Locale.SET_LOGGING_CHANNEL_DESCRIPTION
        execute(ChannelArg) {
            val loggingChannel = args.first

            configuration[loggingChannel.guild.id]!!.loggingConfiguration.loggingChannel = loggingChannel.id
            configuration.save()
            reactSuccess()
        }
    }
}