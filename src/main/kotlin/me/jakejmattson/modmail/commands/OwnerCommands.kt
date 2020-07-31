package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.commands
import me.jakejmattson.modmail.extensions.requiredPermissionLevel
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
import net.dv8tion.jda.api.entities.Activity

@CommandSet("Owner")
fun ownerCommands(configuration: Configuration) = commands {

    requiredPermissionLevel = Permission.BOT_OWNER

    command("SetPrefix") {
        description = "Set the bot's prefix."
        execute(AnyArg("Prefix")) {
            val prefix = it.args.first

            configuration.save()

            it.respond("Prefix set to: $prefix")
        }
    }

    command("SetPresence") {
        description = Locale.SET_PRESENCE_DESCRIPTION
        execute(ChoiceArg("Playing/Watching/Listening", "Playing", "Watching", "Listening").makeOptional("Playing"),
            EveryArg("Presence Message")) {
            val (choice, text) = it.args

            it.discord.jda.presence.activity =
                when (choice.toLowerCase()) {
                    "watching" -> Activity.watching(text)
                    "listening" -> Activity.listening(text)
                    else -> Activity.playing(text)
                }

            it.respond("Discord presence updated!")
        }
    }
}