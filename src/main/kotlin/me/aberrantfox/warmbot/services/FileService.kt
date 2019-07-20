package me.aberrantfox.warmbot.services

import com.google.gson.GsonBuilder
import java.io.File

private const val rootFolder = "data"
private const val configFolder = "$rootFolder/config"
private const val persistenceFolder = "$rootFolder/persistence"
private const val macrosFolder = "$persistenceFolder/macros"
const val configFile = "$configFolder/config.json"

val reportsFolder = File("$persistenceFolder/reports")

val macroFile = createParentsAndFile(macrosFolder, "macros.json")
val messagesFile = createParentsAndFile(configFolder, "messages.json")

private fun createParentsAndFile(parent: String, child: String): File {
    File(parent).apply { mkdirs() }
    return File("$parent/$child")
}

val gson = GsonBuilder().setPrettyPrinting().create()!!
fun save(file: File, data: Any) = file.writeText(gson.toJson(data))