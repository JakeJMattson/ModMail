package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import net.dv8tion.jda.api.entities.Guild

data class Macro(var name: String, var message: String)

data class MacroMap(val map: HashMap<String, ArrayList<Macro>> = hashMapOf())

fun getGuildMacros(guild: Guild) = macroMap.map.getOrPut(guild.id) { arrayListOf() }

lateinit var macroMap: MacroMap

@Service
class MacroService {
    init {
        macroMap = loadMacros()
    }

    private fun ArrayList<Macro>.hasMacro(name: String) = this.any { it.name.toLowerCase() == name.toLowerCase() }

    fun addMacro(name: String, message: String, guild: Guild): Pair<Boolean, String> {
        val macroList = getGuildMacros(guild)

        if (macroList.hasMacro(name)) return false to "This macro already exists!"

        macroList.add(Macro(name, message))
        saveMacros()

        return true to "Macro successfully added!\n$name - $message"
    }

    fun removeMacro(macro: Macro, guild: Guild): Pair<Boolean, String> {
        val macroList = getGuildMacros(guild)

        macroList.remove(macro)
        saveMacros()

        return true to "Macro successfully removed! :: ${macro.name}"
    }

    fun listMacros(guild: Guild) = getGuildMacros(guild).map { it.name }.sorted().joinToString(", ")
        .takeIf { it.isNotEmpty() } ?: "<No Macros added>"

    fun editName(macro: Macro, newName: String, guild: Guild): Pair<Boolean, String> {
        val macroList = getGuildMacros(guild)

        if (macroList.hasMacro(newName)) return false to "A macro with that name already exists!"

        val oldName = macro.name
        macro.name = newName
        saveMacros()

        return true to "Macro name updated!\n`$oldName` renamed to `$newName`"
    }

    fun editMessage(macro: Macro, newMessage: String): Pair<Boolean, String> {
        macro.message = newMessage
        saveMacros()

        return true to "Successfully changed macro message!"
    }
}

private fun saveMacros() = save(macroFile, macroMap)

private fun loadMacros() =
    if (macroFile.exists())
        gson.fromJson(macroFile.readText(), MacroMap::class.java)
    else
        MacroMap()