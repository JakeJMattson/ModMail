package me.jakejmattson.modmail.services

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import me.jakejmattson.discordkt.api.dsl.Data

data class LoggingConfiguration(var loggingChannel: Long,
                                val logEdits: Boolean = true,
                                val logCommands: Boolean = true,
                                val logOpen: Boolean = true,
                                val logClose: Boolean = true) {
    suspend fun getLiveChannel(api: Kord) = api.getChannel(Snowflake(loggingChannel)) as? TextChannel
}

data class GuildConfiguration(var prefix: String,
                              var reportCategory: Long,
                              var archiveChannel: Long,
                              var staffRoleId: Long,
                              val loggingConfiguration: LoggingConfiguration) {
    suspend fun getLiveReportCategory(api: Kord) = api.getChannel(Snowflake(reportCategory)) as? Category
    suspend fun getLiveArchiveChannel(api: Kord) = api.getChannel(Snowflake(archiveChannel)) as? TextChannel
}

data class Configuration(val ownerId: Long = 0,
                         val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()) : Data(configFile) {
    operator fun get(id: Long?) = id?.let { guildConfigurations[it] }
}