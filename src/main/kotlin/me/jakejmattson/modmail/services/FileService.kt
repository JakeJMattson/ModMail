package me.jakejmattson.modmail.services

import java.io.File

private const val rootFolder = "data"
private const val configFolder = "$rootFolder/config"
private const val persistenceFolder = "$rootFolder/persistence"
private const val macrosFolder = "$persistenceFolder/macros"
val configFile = File("$configFolder/config.json")

val reportsFolder = createDirectories("$persistenceFolder/reports")
val macroFile = File(macrosFolder, "macros.json").createParentsAndFile()
val messagesFile = File(configFolder, "messages.json").createParentsAndFile()

private fun File.createParentsAndFile(): File {
    createDirectories(parent)
    return this
}

private fun createDirectories(parentPath: String) = File(parentPath).apply { mkdirs() }