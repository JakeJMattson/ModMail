package me.jakejmattson.modmail.services

import com.gitlab.kordlib.core.entity.Guild
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.api.annotations.Service

@Serializable
data class Macro(var name: String, var message: String)

@Serializable
data class MacroMap(val map: HashMap<String, ArrayList<Macro>> = hashMapOf())

@Service
class MacroService {
    private val macroMap = loadMacros()

    private fun ArrayList<Macro>.hasMacro(name: String) = this.any { it.name.toLowerCase() == name.toLowerCase() }

    fun getGuildMacros(guild: Guild) = macroMap.map.getOrPut(guild.id.value) { arrayListOf() }

    fun addMacro(name: String, message: String, guild: Guild): Boolean {
        val macroList = getGuildMacros(guild)

        if (macroList.hasMacro(name)) return false

        macroList.add(Macro(name, message))
        saveMacros(macroMap)

        return true
    }

    fun removeMacro(macro: Macro, guild: Guild): Boolean {
        val macroList = getGuildMacros(guild)

        macroList.remove(macro)
        saveMacros(macroMap)

        return true
    }

    fun listMacros(guild: Guild) = getGuildMacros(guild).map { it.name }.sorted().joinToString(", ")
        .takeIf { it.isNotEmpty() } ?: "<No Macros added>"

    fun editName(macro: Macro, newName: String, guild: Guild): Pair<Boolean, String> {
        val macroList = getGuildMacros(guild)

        if (macroList.hasMacro(newName)) return false to "A macro with that name already exists!"

        val oldName = macro.name
        macro.name = newName
        saveMacros(macroMap)

        return true to "Macro name updated!\n`$oldName` renamed to `$newName`"
    }

    fun editMessage(macro: Macro, newMessage: String): Pair<Boolean, String> {
        macro.message = newMessage
        saveMacros(macroMap)

        return true to "Successfully changed macro message!"
    }
}

private fun saveMacros(macros: MacroMap) = macroFile.writeText(Json.encodeToString(macros))

private fun loadMacros() =
    if (macroFile.exists())
        Json.decodeFromString(macroFile.readText())
    else
        MacroMap()