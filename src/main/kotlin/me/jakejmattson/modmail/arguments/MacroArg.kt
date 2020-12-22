package me.jakejmattson.modmail.arguments

import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.modmail.services.*

open class MacroArg(override val name: String = "Macro") : ArgumentType<Macro> {
    companion object : MacroArg()

    override suspend fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Macro> {
        val macroService = event.discord.getInjectionObjects(MacroService::class)

        val macro = macroService.getGuildMacros(event.guild!!)
            .firstOrNull { it.name.equals(arg, true) }
            ?: return Error("Unknown Macro")

        return Success(macro)
    }

    override suspend fun generateExamples(event: CommandEvent<*>): List<String> {
        val macroService = event.discord.getInjectionObjects(MacroService::class)
        return macroService.getGuildMacros(event.guild!!).map { it.name }.takeIf { it.isNotEmpty() }
            ?: listOf("<No Macros>")
    }
}
