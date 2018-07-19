package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.Category
import net.dv8tion.jda.core.entities.TextChannel


@CommandSet("Configuration")
fun configurationCommands(conversationService: ConversationService, configuration: Configuration) = commands {
    command("setReportCategory") {
        expect(ChannelCategoryArg)
        execute {
            val reportCategory = it.args.component1() as Category
            val eventChannel = it.channel as TextChannel

            if (configuration.guildConfigurations.find { gc -> eventChannel.guild.id == gc.guildId } != null) {

            } else {
                val guildConfiguration =
                    GuildConfiguration(eventChannel.guild.id, reportCategory.id, "", "!", "Staff")
                configuration.guildConfigurations.add(guildConfiguration)
                saveConfiguration(configuration)
            }
            return@execute
        }
    }
    command("setup") {
        execute {
            val eventChannel = it.channel as TextChannel
            conversationService.createConversation(it.author.id, eventChannel.guild.id, "guild-setup")
            return@execute
        }
    }
}

