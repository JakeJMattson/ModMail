package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.arguments.GuildArg
import me.aberrantfox.warmbot.extensions.idToGuild
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*

@CommandSet("Owner")
fun ownerCommands(configuration: Configuration, guildService: GuildService, persistenceService: PersistenceService) = commands {
    command("Whitelist") {
        requiresGuild = true
        description = Locale.messages.WHITELIST_DESCRIPTION
        expect(GuildArg)
        execute {
            val targetGuild = it.args.component1() as Guild

            if (configuration.whitelist.contains(targetGuild.id))
                return@execute it.respond("${targetGuild.name} (${targetGuild.id}) is already whitelisted.")

            configuration.whitelist.add(targetGuild.id)
            persistenceService.save(configuration)
            it.respond("Successfully added `${targetGuild.name}` to the whitelist.")
        }
    }

    command("UnWhitelist") {
        requiresGuild = true
        description = Locale.messages.UNWHITELIST_DESCRIPTION
        expect(GuildArg)
        execute {
            val targetGuild = it.args.component1() as Guild

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
        description = Locale.messages.SHOW_WHITELIST_DESCRIPTION
        execute {
            it.respond(
                buildString {
                    configuration.whitelist.forEach {
                        val guild = it.idToGuild()
                        this.appendln("${guild.id} (${guild.name})")
                    }
                }
            )
        }
    }

    command("SetPresence") {
        requiresGuild = true
        description = Locale.messages.SET_PRESENCE_DESCRIPTION
        expect(arg(ChoiceArg("Playing/Watching/Listening", "Playing", "Watching", "Listening"), optional = true, default = "Playing"),
            arg(SentenceArg("Presence Message")))
        execute {
            val choice = it.args.component1() as String
            val text = it.args.component2() as String

            it.jda.presence.game =
                when(choice.toLowerCase()) {
                    "watching" -> Game.watching(text)
                    "listening" -> Game.listening(text)
                    else -> Game.playing(text)
                }

            it.respond("Discord presence updated!")
        }
    }
}