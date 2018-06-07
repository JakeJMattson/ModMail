package me.aberrantfox.warmbot.services

import com.google.gson.GsonBuilder
import java.io.File

data class GuildConfiguration(val guildId: String = "insert-id",
                              val reportCategory: String = "insert-id",
                              val archiveChannel: String = "insert-id",
                              val prefix: String = "!",
                              val staffRoleName: String = "Staff",
                              val maxOpenReports: Int = 50)

data class Configuration(val token: String,
                         val guildConfigurations: List<GuildConfiguration>)

private val gson = GsonBuilder().setPrettyPrinting().create()
private val configDir = File("config/")
private val configFile = File("config/config.json")

fun loadConfiguration(): Configuration? {
    if (!(configFile.exists())) {
        configDir.mkdirs()
        configFile.writeText(gson.toJson(Configuration("insert-token-here", mutableListOf(GuildConfiguration()))))
        return null
    }

    return gson.fromJson(configFile.readText(), Configuration::class.java)
}

fun saveConfiguration(config: Configuration) = configFile.writeText(gson.toJson(config))