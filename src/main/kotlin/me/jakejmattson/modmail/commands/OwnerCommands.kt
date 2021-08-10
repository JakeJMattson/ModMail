package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.modmail.arguments.Presence
import me.jakejmattson.modmail.arguments.PresenceArg
import me.jakejmattson.modmail.extensions.reactSuccess
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.Permission

@Suppress("unused")
fun ownerCommands(configuration: Configuration) = commands("Owner", Permission.BOT_OWNER) {
    guildCommand("SetPrefix") {
        description = "Set the bot's prefix."
        execute(AnyArg("Prefix")) {
            configuration[guild]?.prefix = args.first
            configuration.save()
            reactSuccess()
        }
    }

    guildCommand("SetPresence") {
        description = Locale.SET_PRESENCE_DESCRIPTION
        execute(PresenceArg().optional(Presence.PLAYING), EveryArg("Text")) {
            val (choice, text) = args
            choice.apply(discord.kord, text)
            reactSuccess()
        }
    }
}