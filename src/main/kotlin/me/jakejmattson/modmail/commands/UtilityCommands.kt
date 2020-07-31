package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.dsl.command.commands
import me.jakejmattson.discordkt.api.extensions.stdlib.toTimeString
import me.jakejmattson.modmail.messages.Locale
import java.util.Date

private val startTime = Date()

@CommandSet("Utility")
fun utilityCommands() = commands {
    command("Status", "Ping") {
        description = Locale.STATUS_DESCRIPTION
        execute { event ->
            val jda = event.discord.jda

            jda.restPing.queue { restPing ->
                event.respond {
                    color = infoColor

                    val seconds = (Date().time - startTime.time) / 1000

                    addField("Rest ping", "${restPing}ms")
                    addField("Gateway ping", "${jda.gatewayPing}ms")
                    addField("Total Uptime", seconds.toTimeString())
                }
            }
        }
    }
}