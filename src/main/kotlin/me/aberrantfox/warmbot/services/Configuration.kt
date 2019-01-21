package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Data

data class LoggingConfiguration(val loggingChannel: String = "insert-id",
                                val logStartup: Boolean = true,
                                val logMemberOpen: Boolean = true,
                                val logStaffOpen: Boolean = true,
                                val logArchive: Boolean = true,
                                val logClose: Boolean = true)

data class GuildConfiguration(var guildId: String = "insert-id",
                              var reportCategory: String = "insert-id",
                              var archiveChannel: String = "insert-id",
                              var staffRoleName: String = "Staff",
                              var loggingConfiguration: LoggingConfiguration? = LoggingConfiguration())

@Data("config/config.json")
data class Configuration(val prefix: String = "!",
                         val maxOpenReports: Int = 50,
                         val recoverReports: Boolean = true,
                         var guildConfigurations: MutableList<GuildConfiguration> = mutableListOf(GuildConfiguration())) {
    fun hasGuildConfig(guildId: String) = getGuildConfig(guildId) != null
    fun getGuildConfig(guildId: String) = guildConfigurations.firstOrNull { it.guildId == guildId }
}