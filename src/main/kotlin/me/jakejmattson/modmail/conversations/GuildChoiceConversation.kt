package me.jakejmattson.modmail.conversations

import me.jakejmattson.discordkt.api.arguments.IntegerRangeArg
import me.jakejmattson.discordkt.api.dsl.conversation.*
import me.jakejmattson.modmail.services.ReportService
import net.dv8tion.jda.api.entities.*

class GuildChoiceConversation(private val reportService: ReportService) : Conversation() {
    @Start
    fun start(guilds: List<Guild>, message: Message) = conversation {
        val guildIndex = promptEmbed(IntegerRangeArg(1, guilds.size)) {
            simpleTitle = "Select Server"
            description = "Respond with the server you want to contact."
            thumbnail = discord.jda.selfUser.effectiveAvatarUrl
            color = infoColor

            guilds.forEachIndexed { index, guild ->
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