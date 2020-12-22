package me.jakejmattson.modmail.conversations

import dev.kord.core.entity.*
import me.jakejmattson.discordkt.api.arguments.IntegerRangeArg
import me.jakejmattson.discordkt.api.dsl.conversation
import me.jakejmattson.modmail.services.ReportService

fun guildChoiceConversation(reportService: ReportService, guilds: List<Guild>, message: Message) = conversation {
    val guildIndex = promptEmbed(IntegerRangeArg(1, guilds.size)) {
        title = "Select Server"
        description = "Respond with the server you want to contact."
        thumbnail {
            url = discord.api.getSelf().avatar.url
        }

        guilds.toList().forEachIndexed { index, guild ->
            field {
                name = "${index + 1}) ${guild.name}"
            }
        }
    } - 1

    with(reportService) {
        createReport(user, guilds[guildIndex])
        receiveFromUser(message)
    }
}