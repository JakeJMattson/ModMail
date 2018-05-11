package me.aberrantfox.warmbot.services

import com.google.gson.GsonBuilder
import java.io.File

data class Configuration(val token: String = "token",
                         val reportCategory: String = "insert-id",
                         val archiveChannel: String = "insert-id",
                         val prefix: String = "!",
                         val staffRoleName: String = "Staff",
                         val maxOpenReports: Int = 50)

private val gson = GsonBuilder().setPrettyPrinting().create()
private val configDir = File("config/")
private val configFile = File("config/config.json")

fun loadConfiguration(): Configuration? {
    if( !(configFile.exists()) ) {
        configDir.mkdirs()
        configFile.writeText(gson.toJson(Configuration()))

        return null
    }

    return gson.fromJson(configFile.readText(), Configuration::class.java)
}

fun saveConfiguration(config: Configuration) = configFile.writeText(gson.toJson(config))