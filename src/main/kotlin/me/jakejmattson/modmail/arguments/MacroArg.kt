package me.jakejmattson.modmail.arguments

import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.DiscordContext
import me.jakejmattson.modmail.services.Macro
import me.jakejmattson.modmail.services.MacroService

open class MacroArg(override val name: String = "Macro") : StringArgument<Macro> {
    companion object : MacroArg()

    override val description = "A ModMail macro"

    override suspend fun transform(input: String, context: DiscordContext): Result<Macro> {
        val macroService = context.discord.getInjectionObjects(MacroService::class)

        val macro = macroService.getGuildMacros(context.guild!!)
            .firstOrNull { it.name.equals(input, true) }
            ?: return Error("Unknown Macro")

        return Success(macro)
    }

    override suspend fun generateExamples(context: DiscordContext): List<String> {
        val macroService = context.discord.getInjectionObjects(MacroService::class)

        return macroService.getGuildMacros(context.guild!!).map { it.name }.takeIf { it.isNotEmpty() }
            ?: listOf("<No Macros>")
    }
}
