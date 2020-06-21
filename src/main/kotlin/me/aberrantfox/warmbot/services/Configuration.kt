package me.aberrantfox.warmbot.services

import me.jakejmattson.kutils.api.annotations.Data

data class LoggingConfiguration(var loggingChannel: String = "insert-id",
                                val logEdits: Boolean = true,
                                val logCommands: Boolean = true,
                                val logStartup: Boolean = true,
                                val logMemberOpen: Boolean = true,
                                val logStaffOpen: Boolean = true,
                                val logArchive: Boolean = true,
                                val logClose: Boolean = true)

data class GuildConfiguration(val guildId: String = "insert-id",
                              var reportCategory: String = "insert-id",
                              var archiveChannel: String = "insert-id",
                              var staffRoleName: String = "Staff",
                              val loggingConfiguration: LoggingConfiguration = LoggingConfiguration())

@Data(configFile)
data class Configuration(val ownerId: String = "insert-id",
                         var prefix: String = "!",
                         val maxOpenReports: Int = 50,
                         val guildConfigurations: MutableList<GuildConfiguration> = mutableListOf(GuildConfiguration())) {
    fun hasGuildConfig(guildId: String) = getGuildConfig(guildId) != null
    fun getGuildConfig(guildId: String?) = guildConfigurations.firstOrNull { it.guildId == guildId }
}