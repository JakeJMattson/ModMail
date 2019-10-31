package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.kjdautils.extensions.stdlib.toMinimalTimeString
import me.aberrantfox.warmbot.messages.Locale
import java.awt.Color
import java.util.Date

private val startTime = Date()

@CommandSet("Utility")
fun utilityCommands() = commands {
    command("Ping") {
        requiresGuild = true
        description = Locale.PING_DESCRIPTION
        execute {
            it.respond("JDA ping: ${it.discord.jda.gatewayPing}ms\n")
        }
    }

    command("Uptime") {
        description = Locale.UPTIME_DESCRIPTION
        execute {
            val seconds = (Date().time - startTime.time) / 1000

            it.respond {
                title = "I have been running since"
                description = startTime.toString()
                color = Color.WHITE

                field {
                    name = "That's been"
                    value = seconds.toMinimalTimeString()
                }
            }
        }
    }
}