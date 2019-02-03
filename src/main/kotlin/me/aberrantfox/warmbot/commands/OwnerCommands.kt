package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.stdlib.trimToID
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.Guild

@CommandSet("owner")
fun configurationCommands(configuration: Configuration, persistenceService: PersistenceService) = commands {
    command("Whitelist") {
        requiresGuild = true
        description = "Add a guild to the whitelist."
        expect(GuildArg)
        execute {
            val targetGuild = it.args.component1() as Guild

            if (configuration.whitelist.contains(targetGuild.id)) {
                it.respond("${targetGuild.name} (${targetGuild.id}) is already whitelisted.")
                return@execute
            }

            configuration.whitelist.add(targetGuild.id)
            persistenceService.save(configuration)
            it.respond("Successfully added `${targetGuild.name}` to the whitelist.")
        }
    }

    command("UnWhitelist") {
        requiresGuild = true
        description = "Remove a guild from the whitelist."
        expect(GuildArg)
        execute {
            val targetGuild = it.args.component1() as Guild

            if (!configuration.whitelist.contains(targetGuild.id)) {
                it.respond("${targetGuild.name} (${targetGuild.id}) is not whitelisted.")
                return@execute
            }

            configuration.whitelist.remove(targetGuild.id)
            persistenceService.save(configuration)
            it.respond("Successfully removed `${targetGuild.name}` from the whitelist.")
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

