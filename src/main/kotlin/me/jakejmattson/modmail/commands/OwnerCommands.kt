package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.modmail.extensions.requiredPermissionLevel
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*

fun ownerCommands(configuration: Configuration) = commands("Owner") {
    requiredPermissionLevel = Permission.BOT_OWNER

    guildCommand("SetPrefix") {
        description = "Set the bot's prefix."
        execute(AnyArg("Prefix")) {
            val prefix = args.first

            configuration[guild.id.value]?.prefix = prefix
            configuration.save()

            respond("Prefix set to: $prefix")
        }
    }

    guildCommand("SetPresence") {
        description = Locale.SET_PRESENCE_DESCRIPTION
        execute(ChoiceArg("Playing/Watching/Listening", "Playing", "Watching", "Listening").makeOptional("Playing"),
            EveryArg("Presence Message")) {
            val (choice, text) = args

            discord.api.editPresence {
                when (choice.toLowerCase()) {
                    "watching" -> watching(text)
                    "listening" -> listening(text)
                    else -> playing(text)
                }
            }

            respond("Discord presence updated!")
        }
    }
}