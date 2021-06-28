package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChoiceArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.modmail.extensions.requiredPermissionLevel
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.Permission
import java.util.*

@Suppress("unused")
fun ownerCommands(configuration: Configuration) = commands("Owner") {
    requiredPermissionLevel = Permission.BOT_OWNER

    guildCommand("SetPrefix") {
        description = "Set the bot's prefix."
        execute(AnyArg("Prefix")) {
            val prefix = args.first

            configuration[guild.id]?.prefix = prefix
            configuration.save()

            respond("Prefix set to: $prefix")
        }
    }

    guildCommand("SetPresence") {
        description = Locale.SET_PRESENCE_DESCRIPTION
        execute(ChoiceArg("Playing/Watching/Listening", "Playing", "Watching", "Listening").optional("Playing"),
            EveryArg("Presence Message")) {
            val (choice, text) = args

            discord.kord.editPresence {
                when (choice.lowercase()) {
                    "watching" -> watching(text)
                    "listening" -> listening(text)
                    else -> playing(text)
                }
            }

            respond("Discord presence updated!")
        }
    }
}