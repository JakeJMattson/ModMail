package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import net.dv8tion.jda.core.entities.Guild

data class Macro(val name: String, val message: String)

data class MacroMap(val map: HashMap<String, ArrayList<Macro>> = hashMapOf())

fun getGuildMacros(guildId: String) : ArrayList<Macro> {
    if (!macroMap.map.containsKey(guildId))
        macroMap.map[guildId] = arrayListOf()

    return macroMap.map[guildId]!!
}

val macroMap = MacroMap()

@Service
class MacroService {

    private fun ArrayList<Macro>.getMacroByName(name: String) = this.firstOrNull { it.name.toLowerCase() == name.toLowerCase() }

    fun addMacro(name: String, message: String, guild: Guild): Pair<Boolean, String> {
        val macroList = getGuildMacros(guild.id)

        if (macroList.getMacroByName(name) != null) return false to "This macro already exists!"

        macroList.add(Macro(name, message))

        return true to "Macro successfully added!\n$name - $message"
    }

    fun removeMacro(macro: Macro, guild: Guild): Pair<Boolean, String> {
        val macroList = getGuildMacros(guild.id)
        macroList.remove(macro)

        return true to "Macro successfully removed! :: ${macro.name}"
    }

    fun listMacros(guild: Guild) = getGuildMacros(guild.id).map { it.name }.sorted().joinToString(", ")
        .takeIf { it.isNotEmpty() } ?: "<No Macros added>"
}