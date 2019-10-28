package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.messages.*
import me.aberrantfox.warmbot.services.*

@CommandSet("Configuration")
fun configurationCommands(configuration: Configuration, persistenceService: PersistenceService) = commands {

    requiredPermissionLevel = Permission.GUILD_OWNER

    command("SetReportCategory") {
        requiresGuild = true
        description = Locale.SET_REPORT_CATEGORY_DESCRIPTION
        execute(CategoryArg) {
            val reportCategory = it.args.first

            configuration.getGuildConfig(reportCategory.guild.id)!!.reportCategory = reportCategory.id
            persistenceService.save(configuration)
            it.respond(inject({ SET_REPORT_CATEGORY_SUCCESSFUL }, "categoryName" to reportCategory.name))
        }
    }

    command("SetArchiveChannel") {
        requiresGuild = true
        description = Locale.SET_ARCHIVE_CHANNEL_DESCRIPTION
        execute(TextChannelArg) {
            val archiveChannel = it.args.first

            configuration.getGuildConfig(archiveChannel.guild.id)!!.archiveChannel = archiveChannel.id
            persistenceService.save(configuration)
            it.respond(inject({ SET_ARCHIVE_CHANNEL_SUCCESSFUL }, "archiveChannel" to archiveChannel.name))
        }
    }

    command("SetStaffRole") {
        requiresGuild = true
        description = Locale.SET_STAFF_ROLE_DESCRIPTION
        execute(WordArg) {
            val staffRoleName = it.args.first
            val staffRole = it.discord.jda.getRolesByName(staffRoleName, true).firstOrNull()

            staffRole ?: return@execute it.respond(inject({ FAIL_COULD_NOT_FIND_ROLE }, "staffRoleName" to staffRoleName))

            configuration.getGuildConfig(it.message.guild.id)!!.staffRoleName = staffRole.name
            persistenceService.save(configuration)
            it.respond(inject({ SET_STAFF_ROLE_SUCCESSFUL }, "staffRoleName" to staffRole.name))
        }
    }

    command("SetLoggingChannel") {
        requiresGuild = true
        description = Locale.SET_LOGGING_CHANNEL_DESCRIPTION
        execute(TextChannelArg) {
            val loggingChannel = it.args.first

            configuration.getGuildConfig(loggingChannel.guild.id)!!.loggingConfiguration.loggingChannel = loggingChannel.id
            persistenceService.save(configuration)
            it.respond(inject({ SET_LOGGING_CHANNEL_SUCCESSFUL }, "loggingChannel" to loggingChannel.name))
        }
    }

    command("AddStaffChannel") {
        requiresGuild = true
        description = Locale.ADD_STAFF_CHANNEL_DESCRIPTION
        execute(TextChannelArg) {
            val staffChannel = it.args.first
            val channelId = staffChannel.id

            configuration.getGuildConfig(it.message.guild.id)!!.staffChannels.apply {
                if (channelId in this)
                    return@execute it.respond("Channel already whitelisted!")

                this.add(staffChannel.id)
                persistenceService.save(configuration)

                return@execute it.respond("Successfully whitelisted channel :: ${staffChannel.name}")
            }
        }
    }

    command("RemoveStaffChannel") {
        requiresGuild = true
        description = Locale.REMOVE_STAFF_CHANNEL_DESCRIPTION
        execute(TextChannelArg) {
            val staffChannel = it.args.first
            val channelId = staffChannel.id

            configuration.getGuildConfig(it.message.guild.id)!!.staffChannels.apply {
                if (channelId !in this)
                    return@execute it.respond("Channel not whitelisted!")

                this.remove(staffChannel.id)
                persistenceService.save(configuration)

                return@execute it.respond("Successfully unwhitelisted channel :: ${staffChannel.name}")
            }
        }
    }

    command("ListStaffChannels") {
        requiresGuild = true
        description = Locale.LIST_STAFF_CHANNELS_DESCRIPTION
        execute {
            val staffChannels = configuration.getGuildConfig(it.message.guild.id)!!.staffChannels

            it.respond(staffChannels.joinToString("\n") { "${it.idToTextChannel()?.asMention} - $it" })
        }
    }
}