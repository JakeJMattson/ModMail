package me.jakejmattson.modmail.services

import dev.kord.core.entity.Guild
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.api.annotations.Service

@Serializable
data class Macro(var name: String, var message: String)

@Service
class MacroService {
    private val macroMap = loadMacros()

    private fun MutableList<Macro>.hasMacro(name: String) = this.any { it.name.equals(name, true) }

    fun getGuildMacros(guild: Guild) = macroMap.getOrPut(guild.id.asString) { arrayListOf() }

    fun addMacro(name: String, message: String, guild: Guild): Boolean {
        val macroList = getGuildMacros(guild)

        if (macroList.hasMacro(name)) return false

        macroList.add(Macro(name, message))
        macroMap.save()

        return true
    }

    fun removeMacro(macro: Macro, guild: Guild) {
        getGuildMacros(guild).remove(macro)
        macroMap.save()
    }

    fun listMacros(guild: Guild) = getGuildMacros(guild)
        .map { it.name }
        .sorted()
        .joinToString()
        .takeIf { it.isNotEmpty() }
        ?: "<No Macros added>"

    fun editName(macro: Macro, newName: String, guild: Guild): Boolean {
        if (getGuildMacros(guild).hasMacro(newName)) return false

        macro.name = newName
        macroMap.save()

        return true
    }

    fun editMessage(macro: Macro, newMessage: String) {
        macro.message = newMessage
        macroMap.save()
    }
}

private fun Map<String, MutableList<Macro>>.save() = macroFile.writeText(Json.encodeToString(this))

private fun loadMacros() =
    if (macroFile.exists())
        Json.decodeFromString(macroFile.readText())
    else
        mutableMapOf<String, MutableList<Macro>>()