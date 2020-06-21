package me.aberrantfox.warmbot.commands

import me.aberrantfox.warmbot.extensions.requiredPermissionLevel
import me.aberrantfox.warmbot.messages.*
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.annotations.CommandSet
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.command.commands
import me.jakejmattson.kutils.api.services.PersistenceService

@CommandSet("Configuration")
fun configurationCommands(configuration: Configuration, persistenceService: PersistenceService) = commands {

    requiredPermissionLevel = Permission.GUILD_OWNER

    command("SetReportCategory") {
        description = Locale.SET_REPORT_CATEGORY_DESCRIPTION
        execute(CategoryArg) {
            val reportCategory = it.args.first

            configuration.getGuildConfig(reportCategory.guild.id)!!.reportCategory = reportCategory.id
            persistenceService.save(configuration)
            it.respond(inject({ SET_REPORT_CATEGORY_SUCCESSFUL }, "categoryName" to reportCategory.name))
        }
    }

    command("SetArchiveChannel") {
        description = Locale.SET_ARCHIVE_CHANNEL_DESCRIPTION
        execute(TextChannelArg) {
            val archiveChannel = it.args.first

            configuration.getGuildConfig(archiveChannel.guild.id)!!.archiveChannel = archiveChannel.id
            persistenceService.save(configuration)
            it.respond(inject({ SET_ARCHIVE_CHANNEL_SUCCESSFUL }, "archiveChannel" to archiveChannel.name))
        }
    }

    command("SetStaffRole") {
        description = Locale.SET_STAFF_ROLE_DESCRIPTION
        execute(AnyArg) {
            val staffRoleName = it.args.first
            val staffRole = it.discord.jda.getRolesByName(staffRoleName, true).firstOrNull()

            staffRole
                ?: return@execute it.respond(inject({ FAIL_COULD_NOT_FIND_ROLE }, "staffRoleName" to staffRoleName))

            configuration.getGuildConfig(it.message.guild.id)!!.staffRoleName = staffRole.name
            persistenceService.save(configuration)
            it.respond(inject({ SET_STAFF_ROLE_SUCCESSFUL }, "staffRoleName" to staffRole.name))
        }
    }

    command("SetLoggingChannel") {
        description = Locale.SET_LOGGING_CHANNEL_DESCRIPTION
        execute(TextChannelArg) {
            val loggingChannel = it.args.first

            configuration.getGuildConfig(loggingChannel.guild.id)!!.loggingConfiguration.loggingChannel = loggingChannel.id
            persistenceService.save(configuration)
            it.respond(inject({ SET_LOGGING_CHANNEL_SUCCESSFUL }, "loggingChannel" to loggingChannel.name))
        }
    }
}