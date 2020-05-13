package me.aberrantfox.warmbot.arguments

import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.services.*

open class MacroArg(override val name : String = "Macro"): ArgumentType<Macro>() {
    companion object : MacroArg()

    override val consumptionType = ConsumptionType.Single
    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Macro> {
        val macroService = event.discord.getInjectionObject<MacroService>()
            ?: return ArgumentResult.Error("Something went wrong while searching for macros.")

        val macro = macroService.getGuildMacros(event.guild!!).firstOrNull { it.name.toLowerCase() == arg.toLowerCase() }
            ?: return ArgumentResult.Error("No such macro in this guild!")

        return ArgumentResult.Success(macro)
    }

    override fun generateExamples(event: CommandEvent<*>): List<String> {
        val macroService = event.discord.getInjectionObject<MacroService>()!!
        val macros = macroService.getGuildMacros(event.guild!!).map { it.name }

        return macros.takeIf { it.isNotEmpty() } ?: listOf("<No Macros>")
    }
}
