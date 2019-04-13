package me.aberrantfox.warmbot.arguments

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.services.getGuildMacros

open class MacroArg(override val name : String = "Macro") : ArgumentType {
    companion object : MacroArg()

    override val examples = arrayListOf("ask", "wrapcode", "cc", "date")
    override val consumptionType = ConsumptionType.Single
    override fun convert(arg: String, args: List<String>, event: CommandEvent): ArgumentResult {
        val macro = getGuildMacros(event.guild!!.id).firstOrNull { it.name.toLowerCase() == arg.toLowerCase() }
            ?: return ArgumentResult.Error("No such macro in this guild!")

        return ArgumentResult.Single(macro)
    }
}
