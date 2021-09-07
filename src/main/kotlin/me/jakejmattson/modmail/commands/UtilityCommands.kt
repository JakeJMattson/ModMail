package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.commands.commands
import me.jakejmattson.discordkt.api.extensions.addField
import me.jakejmattson.discordkt.api.extensions.toTimeString
import me.jakejmattson.modmail.messages.Locale
import java.util.*
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

private val startTime = Date()

@Suppress("unused")
@ExperimentalTime
fun utilityCommands() = commands("Utility") {
    slash("Status") {
        description = Locale.STATUS_DESCRIPTION
        execute {
            respond {
                val seconds = (Date().time - startTime.time) / 1000

                addField("Gateway Ping", discord.kord.gateway.averagePing?.toDouble(DurationUnit.MILLISECONDS)?.roundToInt().toString())
                addField("Total Uptime", seconds.toTimeString())
            }
        }
    }
}