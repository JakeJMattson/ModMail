package me.aberrantfox.warmbot.commands

import me.jakejmattson.kutils.api.annotations.CommandSet
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.command.*
import me.jakejmattson.kutils.api.services.PersistenceService
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.entities.Activity

@CommandSet("Owner")
fun ownerCommands(configuration: Configuration, prefixService: PrefixService, persistenceService: PersistenceService) = commands {

    requiredPermissionLevel = Permission.BOT_OWNER

    command("SetPrefix") {
        description = "Set the bot's prefix."
        execute(AnyArg("Prefix")) {
            val prefix = it.args.first

            prefixService.setPrefix(prefix)
            persistenceService.save(configuration)

            it.respond("Prefix set to: $prefix")
        }
    }

    command("SetPresence") {
        requiresGuild = true
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