package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.messages.Locale
import java.util.*
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

private val startTime = Date()

@Suppress("unused")
@ExperimentalTime
fun utilityCommands() = commands("Utility") {
    guildCommand("Status", "Ping") {
        description = Locale.STATUS_DESCRIPTION
        execute {
            respond {
                val seconds = (Date().time - startTime.time) / 1000

                addField("Gateway Ping", discord.kord.gateway.averagePing?.inMilliseconds?.roundToInt().toString())
                addField("Total Uptime", seconds.toTimeString())
            }
        }
    }
}