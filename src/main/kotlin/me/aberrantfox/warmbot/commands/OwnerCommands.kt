package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.entities.Activity

@CommandSet("Owner")
fun ownerCommands(configuration: Configuration, prefixService: PrefixService, guildService: GuildService, persistenceService: PersistenceService) = commands {

    requiredPermissionLevel = Permission.BOT_OWNER

    command("Whitelist") {
        requiresGuild = true
        description = Locale.WHITELIST_DESCRIPTION
        execute(GuildArg) {
            val targetGuild = it.args.first

            if (configuration.whitelist.contains(targetGuild.id))
                return@execute it.respond("${targetGuild.name} (${targetGuild.id}) is already whitelisted.")

            configuration.whitelist.add(targetGuild.id)
            persistenceService.save(configuration)
            it.respond("Successfully added `${targetGuild.name}` to the whitelist.")
        }
    }

    command("UnWhitelist") {
        requiresGuild = true
        description = Locale.UNWHITELIST_DESCRIPTION
        execute(GuildArg) {
            val targetGuild = it.args.first

            if (!configuration.whitelist.contains(targetGuild.id))
                return@execute it.respond("${targetGuild.name} (${targetGuild.id}) is not whitelisted.")

            configuration.whitelist.remove(targetGuild.id)
            persistenceService.save(configuration)
            it.respond("Successfully removed `${targetGuild.name}` from the whitelist.")

            guildService.cleanseGuilds()
        }
    }

    command("ShowWhitelist") {
        requiresGuild = true
        description = Locale.SHOW_WHITELIST_DESCRIPTION
        execute {
            it.respond(
                buildString {
                    configuration.whitelist.forEach {
                        val guild = it.idToGuild()!!
                        this.appendln("${guild.id} (${guild.name})")
                    }
                }
            )
        }
    }

    command("SetPrefix") {
        description = "Set the bot's prefix."
        execute(WordArg("Prefix")) {
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
            SentenceArg("Presence Message"))  {
            val (choice, text) = it.args

            it.discord.jda.presence.activity =
                when(choice.toLowerCase()) {
                    "watching" -> Activity.watching(text)
                    "listening" -> Activity.listening(text)
                    else -> Activity.playing(text)
                }

            it.respond("Discord presence updated!")
        }
    }
}