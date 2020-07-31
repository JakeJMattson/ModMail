package me.jakejmattson.modmail.services

import me.jakejmattson.discordkt.api.dsl.data.Data
import net.dv8tion.jda.api.JDA

data class LoggingConfiguration(var loggingChannel: Long,
                                val logEdits: Boolean = true,
                                val logCommands: Boolean = true,
                                val logStartup: Boolean = true,
                                val logMemberOpen: Boolean = true,
                                val logStaffOpen: Boolean = true,
                                val logArchive: Boolean = true,
                                val logClose: Boolean = true) {
    fun getLiveChannel(jda: JDA) = jda.getTextChannelById(loggingChannel)
}

data class GuildConfiguration(var prefix: String,
                              var reportCategory: Long,
                              var archiveChannel: Long,
                              var staffRoleId: Long,
                              val loggingConfiguration: LoggingConfiguration) {
    fun getLiveReportCategory(jda: JDA) = jda.getCategoryById(reportCategory)
    fun getLiveArchiveChannel(jda: JDA) = jda.getTextChannelById(archiveChannel)
    fun getLiveRole(jda: JDA) = jda.getRoleById(staffRoleId)
}

data class Configuration(val ownerId: String = "insert-id",
                         val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()) : Data(configFile) {
    operator fun get(id: Long?) = id?.let { guildConfigurations[it] }
}