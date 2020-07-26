package me.jakejmattson.modmail.commands

import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
import me.jakejmattson.kutils.api.annotations.CommandSet
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.command.commands

@CommandSet("Configuration")
fun configurationCommands(configuration: Configuration) = commands {

    requiredPermissionLevel = Permission.GUILD_OWNER

    command("SetReportCategory") {
        description = Locale.SET_REPORT_CATEGORY_DESCRIPTION
        execute(CategoryArg) {
            val reportCategory = it.args.first

            configuration.getGuildConfig(reportCategory.guild.id)!!.reportCategory = reportCategory.id
            configuration.save()
            it.reactSuccess()
        }
    }

    command("SetArchiveChannel") {
        description = Locale.SET_ARCHIVE_CHANNEL_DESCRIPTION
        execute(TextChannelArg) {
            val archiveChannel = it.args.first

            configuration.getGuildConfig(archiveChannel.guild.id)!!.archiveChannel = archiveChannel.id
            configuration.save()
            it.reactSuccess()
        }
    }

    command("SetStaffRole") {
        description = Locale.SET_STAFF_ROLE_DESCRIPTION
        execute(RoleArg) {
            val staffRole = it.args.first

            configuration.getGuildConfig(it.message.guild.id)!!.staffRoleName = staffRole.name
            configuration.save()
            it.reactSuccess()
        }
    }

    command("SetLoggingChannel") {
        description = Locale.SET_LOGGING_CHANNEL_DESCRIPTION
        execute(TextChannelArg) {
            val loggingChannel = it.args.first

            configuration.getGuildConfig(loggingChannel.guild.id)!!.loggingConfiguration.loggingChannel = loggingChannel.id
            configuration.save()
            it.reactSuccess()
        }
    }
}