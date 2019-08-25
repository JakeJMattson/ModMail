package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.extensions.idToGuild
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.entities.*

@CommandSet("Owner")
fun ownerCommands(configuration: Configuration, prefixService: PrefixService, guildService: GuildService, persistenceService: PersistenceService) = commands {
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
                        val guild = it.idToGuild()!!
                        this.appendln("${guild.id} (${guild.name})")
                    }
                }
            )
        }
    }

    command("SetPrefix") {
        description = "Set the bot's prefix."
        expect(WordArg("Prefix"))
        execute {
            val prefix = it.args.component1() as String

            prefixService.setPrefix(prefix)
            persistenceService.save(configuration)

            it.respond("Prefix set to: $prefix")
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