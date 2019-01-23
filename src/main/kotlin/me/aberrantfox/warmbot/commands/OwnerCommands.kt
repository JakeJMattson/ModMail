package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.stdlib.trimToID
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.Guild

@CommandSet("owner")
fun configurationCommands(configuration: Configuration) = commands {
    command("whitelist") {
        requiresGuild = true
        expect(GuildArg)
        description = "Test the owner precondition."
        execute {
            val targetGuild = it.args.component1() as Guild
            it.respond("Successfully whitelisted ${targetGuild.name}")
        }
    }
}

open class GuildArg(override val name: String = "ChannelCategory") : ArgumentType {
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

