package me.jakejmattson.modmail.conversations

import com.gitlab.kordlib.core.entity.*
import me.jakejmattson.discordkt.api.arguments.IntegerRangeArg
import me.jakejmattson.discordkt.api.dsl.*
import me.jakejmattson.modmail.services.ReportService

class GuildChoiceConversation(private val reportService: ReportService) : Conversation() {
    @Start
    fun start(guilds: List<Guild>, message: Message) = conversation {
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
}