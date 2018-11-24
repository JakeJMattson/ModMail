package me.aberrantfox.warmbot.services

import com.google.gson.GsonBuilder
import java.io.File

data class LoggingConfiguration(var loggingChannel: String = "insert-id",
                                val logStartup: Boolean = true,
                                var logMemberOpen: Boolean = true,
                                var logArchive: Boolean = true,
                                var logClose: Boolean = true)

data class GuildConfiguration(var guildId: String = "insert-id",
                              var reportCategory: String = "insert-id",
                              var archiveChannel: String = "insert-id",
                              var prefix: String = "!",
                              var staffRoleName: String = "Staff",
                              var loggingConfiguration: LoggingConfiguration? = LoggingConfiguration())

data class Configuration(val token: String,
                         val maxOpenReports: Int,
                         val recoverReports: Boolean,
                         var guildConfigurations: MutableList<GuildConfiguration>)

private val gson = GsonBuilder().setPrettyPrinting().create()
private val configDir = File("config/")
private val configFile = File("config/config.json")

fun loadConfiguration(): Configuration? {
    if (!(configFile.exists())) {
        configDir.mkdirs()
        configFile.writeText(gson.toJson(Configuration("insert-token-here", 50, true, mutableListOf(GuildConfiguration()))))
        return null
    }
    return gson.fromJson(configFile.readText(), Configuration::class.java)
}

fun saveConfiguration(config: Configuration) = configFile.writeText(gson.toJson(config))

fun hasGuildConfiguration(guildConfigurations: List<GuildConfiguration>,
                          guildId: String) = guildConfigurations.any { g -> g.guildId == guildId }