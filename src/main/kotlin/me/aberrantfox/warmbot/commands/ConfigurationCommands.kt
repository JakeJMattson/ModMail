package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.*

@CommandSet("configuration")
fun configurationCommands(conversationService: ConversationService, configuration: Configuration) = commands {
    command("setreportcategory") {
        description = Locale.messages.SET_REPORT_CATEGORY_DESCRIPTION
        expect(ChannelCategoryArg)
        execute {
            val reportCategory = it.args.component1() as Category
            val guildConfig = configuration.getGuildConfig(reportCategory.guild.id)

            if (guildConfig == null) {
                displayNoConfig(it)
                return@execute
            }

            guildConfig.reportCategory = reportCategory.id
            configuration.save()
            val response = Locale.inject({REPORT_ARCHIVE_SUCCESSFUL}, "reportName" to reportCategory.name)
            it.respond(response)

            return@execute
        }
    }

    command("setarchivechannel") {
        description = Locale.messages.SET_ARCHIVE_CHANNEL_DESCRIPTION
        expect(TextChannelArg)
        execute {
            val archiveChannel = it.args.component1() as TextChannel
            val guildConfig = configuration.getGuildConfig(archiveChannel.guild.id)

            if (guildConfig == null) {
                displayNoConfig(it)
                return@execute
            }

            guildConfig.archiveChannel = archiveChannel.id
            configuration.save()
            val response = Locale.inject({ ARCHIVE_CHANNEL_SET_SUCCESSFUL }, "archiveChannel" to archiveChannel.name)
            it.respond(response)

            return@execute
        }
    }

    command("setstaffrole") {
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

            val guildConfig = configuration.getGuildConfig(it.message.guild.id)

            if (guildConfig == null) {
                displayNoConfig(it)
                return@execute
            }
            
            guildConfig.staffRoleName = staffRole.name
            configuration.save()
            val response = Locale.inject({ SET_STAFF_ROLE_SUCCESSFUL },"staffRoleName" to staffRole.name)
            it.respond(response)

            return@execute
        }
    }

    command("setup") {
        description = Locale.messages.SETUP_DESCRIPTION
        execute {
            val guildId = it.guild!!.id

            if (!configuration.hasGuildConfig(guildId)) {
                conversationService.createConversation(it.author.id, guildId, "guild-setup")
            } else {
                it.respond(Locale.messages.SETUP_DESCRIPTION)
            }

            return@execute
        }
    }
}

fun displayNoConfig(event: CommandEvent) = event.respond(Locale.messages.NO_CONFIG)