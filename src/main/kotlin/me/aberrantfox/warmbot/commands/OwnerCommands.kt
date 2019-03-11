package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.stdlib.trimToID
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.extensions.idToGuild
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.Guild

@CommandSet("owner")
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
                StringBuilder().apply {
                    configuration.whitelist.forEach {
                        val guild = it.idToGuild()
                        this.appendln("${guild.id} (${guild.name})")
                    }
                }.toString()
            )
        }
    }
}

open class GuildArg(override val name: String = "Guild") : ArgumentType {
    companion object : GuildArg()

    override val examples = arrayListOf("244230771232079873")
    override val consumptionType = ConsumptionType.Single
    override fun convert(arg: String, args: List<String>, event: CommandEvent): ArgumentResult {
        val retrieved = tryRetrieveSnowflake(event.jda) { it.getGuildById(arg.trimToID()) }
        return when (retrieved) {
            null -> ArgumentResult.Error("Couldn't retrieve guild: $arg")
            else -> ArgumentResult.Single(retrieved)
        }
    }
}

