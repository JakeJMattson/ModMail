package me.jakejmattson.modmail.services

import dev.kord.core.entity.Guild
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.annotations.Service

@Serializable
data class Macro(var name: String, var message: String)

@Service
class MacroService {
    private val macroMap = loadMacros()

    private fun findMacro(guild: Guild, name: String) = getGuildMacros(guild).firstOrNull { it.name.equals(name, true) }

    private fun MutableList<Macro>.hasMacro(name: String) = this.any { it.name.equals(name, true) }

    fun getGuildMacros(guild: Guild) = macroMap.getOrPut(guild.id.toString()) { arrayListOf() }

    fun addMacro(name: String, message: String, guild: Guild): Boolean {
        val macroList = getGuildMacros(guild)

        if (macroList.hasMacro(name)) return false

        macroList.add(Macro(name, message))
        macroMap.save()

        return true
    }

    fun removeMacro(name: String, guild: Guild): Boolean {
        val macro = findMacro(guild, name) ?: return false
        val wasRemoved = getGuildMacros(guild).remove(macro)
        macroMap.save()
        return wasRemoved
    }

    fun listMacros(guild: Guild) = getGuildMacros(guild)
        .map { it.name }
        .sorted()
        .joinToString()
        .takeIf { it.isNotEmpty() }
        ?: "<No Macros added>"

    fun editName(name: String, newName: String, guild: Guild): Boolean {
        if (getGuildMacros(guild).hasMacro(newName)) return false
        val macro = findMacro(guild, name) ?: return false

        macro.name = newName
        macroMap.save()

        return true
    }

    fun editMessage(name: String, newMessage: String, guild: Guild): Boolean {
        val macro = findMacro(guild, name) ?: return false
        macro.message = newMessage
        macroMap.save()
        return true
    }
}

private fun Map<String, MutableList<Macro>>.save() = macroFile.writeText(Json.encodeToString(this))

private fun loadMacros() =
    if (macroFile.exists())
        Json.decodeFromString(macroFile.readText())
    else
        mutableMapOf<String, MutableList<Macro>>()