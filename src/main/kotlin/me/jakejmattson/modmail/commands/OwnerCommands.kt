package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.modmail.arguments.Presence
import me.jakejmattson.modmail.arguments.PresenceArg
import me.jakejmattson.modmail.extensions.reactSuccess
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.Permissions

@Suppress("unused")
fun ownerCommands(configuration: Configuration) = commands("Owner", Permissions.BOT_OWNER) {
     command("Prefix") {
        description = "Set the bot prefix."
        execute(AnyArg("Prefix")) {
            configuration[guild]?.prefix = args.first
            configuration.save()
            reactSuccess()
        }
    }

    command("Presence") {
        description = Locale.SET_PRESENCE_DESCRIPTION
        execute(PresenceArg.optional(Presence.PLAYING), EveryArg("Text")) {
            val (choice, text) = args
            choice.apply(discord.kord, text)
            reactSuccess()
        }
    }
}