package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*

fun configurationCommands(configuration: Configuration) = commands("Configuration") {
    requiredPermissionLevel = Permission.GUILD_OWNER

    guildCommand("SetReportCategory") {
        description = Locale.SET_REPORT_CATEGORY_DESCRIPTION
        execute(CategoryArg) {
            val reportCategory = it.first

            configuration[reportCategory.guild.id.longValue]!!.reportCategory = reportCategory.id.longValue
            configuration.save()
            reactSuccess()
        }
    }

    guildCommand("SetArchiveChannel") {
        description = Locale.SET_ARCHIVE_CHANNEL_DESCRIPTION
        execute(ChannelArg) {
            val archiveChannel = it.first

            configuration[archiveChannel.guild.id.longValue]!!.archiveChannel = archiveChannel.id.longValue
            configuration.save()
            reactSuccess()
        }
    }

    guildCommand("SetStaffRole") {
        description = Locale.SET_STAFF_ROLE_DESCRIPTION
        execute(RoleArg) {
            val staffRole = it.first

            configuration[message.getGuild().id.longValue]!!.staffRoleId = staffRole.id.longValue
            configuration.save()
            reactSuccess()
        }
    }

    guildCommand("SetLoggingChannel") {
        description = Locale.SET_LOGGING_CHANNEL_DESCRIPTION
        execute(ChannelArg) {
            val loggingChannel = it.first

            configuration[loggingChannel.guild.id.longValue]!!.loggingConfiguration.loggingChannel = loggingChannel.id.longValue
            configuration.save()
            reactSuccess()
        }
    }
}