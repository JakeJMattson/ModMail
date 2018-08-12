package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.extensions.stdlib.trimToID
import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import me.aberrantfox.kjdautils.internal.command.ArgumentType
import me.aberrantfox.kjdautils.internal.command.ConsumptionType
import me.aberrantfox.kjdautils.internal.command.tryRetrieveSnowflake

object ChannelCategoryArg : ArgumentType {

    override val name = "Channel Category"
    override val examples = arrayListOf("302134543639511050")
    override val consumptionType = ConsumptionType.Single

    override fun convert(arg: String, args: List<String>, event: CommandEvent): ArgumentResult {
        val retrieved = tryRetrieveSnowflake(event.jda) { it.getCategoryById(arg.trimToID()) }
        return when {
            retrieved == null -> ArgumentResult.Error("Couldn't retrieve channel category: $arg")
            else -> ArgumentResult.Single(retrieved)
        }
    }
}
