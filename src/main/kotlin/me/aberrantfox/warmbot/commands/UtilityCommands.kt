package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.warmbot.extensions.toMinimalTimeString
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.InfoService
import net.dv8tion.jda.api.entities.TextChannel
import java.awt.Color
import java.util.Date

private val startTime = Date()

@CommandSet("Utility")
fun utilityCommands(infoService: InfoService) = commands {
    command("Ping") {
        requiresGuild = true
        description = Locale.messages.PING_DESCRIPTION
        execute {
            it.respond("JDA ping: ${it.discord.jda.gatewayPing}ms\n")
        }
    }

    command("BotInfo") {
        description = Locale.messages.BOT_INFO_DESCRIPTION
        execute {
            val channel = it.channel

            if (channel is TextChannel)
                it.respond(infoService.botInfo(channel))
        }
    }

    command("Uptime") {
        description = "Displays how long the bot has been running."
        execute {
            val seconds = (Date().time - startTime.time) / 1000

            it.respond(embed {
                title = "I have been running since"
                description = startTime.toString()
                color = Color.WHITE

                field {
                    name = "That's been"
                    value = seconds.toMinimalTimeString()
                }
            })
        }
    }
}