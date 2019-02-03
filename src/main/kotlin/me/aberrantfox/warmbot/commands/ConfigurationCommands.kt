package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.*

@CommandSet("configuration")
fun configurationCommands(configuration: Configuration, persistenceService: PersistenceService) = commands {
    command("SetReportCategory") {
        requiresGuild = true
        description = Locale.messages.SET_REPORT_CATEGORY_DESCRIPTION
        expect(ChannelCategoryArg)
        execute {
            val reportCategory = it.args.component1() as Category

            configuration.getGuildConfig(reportCategory.guild.id)!!.reportCategory = reportCategory.id
            persistenceService.save(configuration)
            val response = Locale.inject({REPORT_ARCHIVE_SUCCESSFUL}, "reportName" to reportCategory.name)
            it.respond(response)

            return@execute
        }
    }

    command("SetArchiveChannel") {
        requiresGuild = true
        description = Locale.messages.SET_ARCHIVE_CHANNEL_DESCRIPTION
        expect(TextChannelArg)
        execute {
            val archiveChannel = it.args.component1() as TextChannel

            configuration.getGuildConfig(archiveChannel.guild.id)!!.archiveChannel = archiveChannel.id
            persistenceService.save(configuration)
            val response = Locale.inject({ ARCHIVE_CHANNEL_SET_SUCCESSFUL }, "archiveChannel" to archiveChannel.name)
            it.respond(response)

            return@execute
        }
    }

    command("SetStaffRole") {
        requiresGuild = true
        description = Locale.messages.SET_STAFF_ROLE_DESCRIPTION
        expect(WordArg)
        execute {
            val staffRoleName = it.args.component1() as String
            val staffRole = it.jda.getRolesByName(staffRoleName, true).firstOrNull()

            if (staffRole == null) {
                val response = Locale.inject({ COULD_NOT_FIND_ROLE }, "staffRoleName" to staffRoleName)
                it.respond(response)
                return@execute
            }

            configuration.getGuildConfig(it.message.guild.id)!!.staffRoleName = staffRole.name
            persistenceService.save(configuration)
            val response = Locale.inject({ SET_STAFF_ROLE_SUCCESSFUL },"staffRoleName" to staffRole.name)
            it.respond(response)

            return@execute
        }
    }
}