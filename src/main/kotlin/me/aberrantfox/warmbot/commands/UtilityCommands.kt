package me.aberrantfox.warmbot.commands

import me.aberrantfox.warmbot.messages.Locale
import me.jakejmattson.kutils.api.annotations.CommandSet
import me.jakejmattson.kutils.api.dsl.command.*
import me.jakejmattson.kutils.api.extensions.stdlib.toTimeString
import java.util.Date

private val startTime = Date()

@CommandSet("Utility")
fun utilityCommands() = commands {
    command("Status", "Ping", "Uptime") {
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