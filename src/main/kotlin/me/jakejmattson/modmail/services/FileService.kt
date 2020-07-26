package me.jakejmattson.modmail.services

import com.google.gson.GsonBuilder
import java.io.File

private const val rootFolder = "data"
private const val configFolder = "$rootFolder/config"
private const val persistenceFolder = "$rootFolder/persistence"
private const val macrosFolder = "$persistenceFolder/macros"
const val configFile = "$configFolder/config.json"

val reportsFolder = createDirectories("$persistenceFolder/reports")
val macroFile = File(macrosFolder, "macros.json").createParentsAndFile()
val messagesFile = File(configFolder, "messages.json").createParentsAndFile()

private fun File.createParentsAndFile(): File {
    createDirectories(parent)
    return this
}

private fun createDirectories(parentPath: String) = File(parentPath).apply { mkdirs() }

val gson = GsonBuilder().setPrettyPrinting().create()!!
fun save(file: File, data: Any) = file.writeText(gson.toJson(data))