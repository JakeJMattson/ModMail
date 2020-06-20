package me.aberrantfox.warmbot.arguments

import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.dsl.arguments.*
import me.jakejmattson.kutils.api.dsl.command.CommandEvent

open class MacroArg(override val name: String = "Macro") : ArgumentType<Macro>() {
    companion object : MacroArg()

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Macro> {
        val macroService = event.discord.getInjectionObjects(MacroService::class)

        val macro = macroService.getGuildMacros(event.guild!!).firstOrNull { it.name.toLowerCase() == arg.toLowerCase() }
            ?: return ArgumentResult.Error("No such macro in this guild!")

        return ArgumentResult.Success(macro)
    }

    override fun generateExamples(event: CommandEvent<*>): List<String> {
        val macroService = event.discord.getInjectionObjects(MacroService::class)

        return macroService.getGuildMacros(event.guild!!).map { it.name }.takeIf { it.isNotEmpty() }
            ?: listOf("<No Macros>")
    }
}
