package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.commands
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*

@CommandSet("Configuration")
fun configurationCommands(configuration: Configuration) = commands {

    requiredPermissionLevel = Permission.GUILD_OWNER

    command("SetReportCategory") {
        description = Locale.SET_REPORT_CATEGORY_DESCRIPTION
        execute(CategoryArg) {
            val reportCategory = it.args.first

            configuration[reportCategory.guild.idLong]!!.reportCategory = reportCategory.idLong
            configuration.save()
            it.reactSuccess()
        }
    }

    command("SetArchiveChannel") {
        description = Locale.SET_ARCHIVE_CHANNEL_DESCRIPTION
        execute(TextChannelArg) {
            val archiveChannel = it.args.first

            configuration[archiveChannel.guild.idLong]!!.archiveChannel = archiveChannel.idLong
            configuration.save()
            it.reactSuccess()
        }
    }

    command("SetStaffRole") {
        description = Locale.SET_STAFF_ROLE_DESCRIPTION
        execute(RoleArg) {
            val staffRole = it.args.first

            configuration[it.message.guild.idLong]!!.staffRoleId = staffRole.idLong
            configuration.save()
            it.reactSuccess()
        }
    }

    command("SetLoggingChannel") {
        description = Locale.SET_LOGGING_CHANNEL_DESCRIPTION
        execute(TextChannelArg) {
            val loggingChannel = it.args.first

            configuration[loggingChannel.guild.idLong]!!.loggingConfiguration.loggingChannel = loggingChannel.idLong
            configuration.save()
            it.reactSuccess()
        }
    }
}