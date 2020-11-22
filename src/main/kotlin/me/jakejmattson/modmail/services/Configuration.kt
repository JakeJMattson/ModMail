package me.jakejmattson.modmail.services

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.channel.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.encodeToString
import me.jakejmattson.discordkt.api.dsl.Data

@Serializable
data class LoggingConfiguration(var loggingChannel: Snowflake,
                                val logEdits: Boolean = true,
                                val logCommands: Boolean = true,
                                val logOpen: Boolean = true,
                                val logClose: Boolean = true) {
    suspend fun getLiveChannel(api: Kord) = api.getChannel(loggingChannel) as? TextChannel
}

@Serializable
data class GuildConfiguration(var prefix: String,
                              var reportCategory: Snowflake,
                              var archiveChannel: Snowflake,
                              var staffRoleId: Snowflake,
                              val loggingConfiguration: LoggingConfiguration) {
    suspend fun getLiveReportCategory(api: Kord) = api.getChannel(reportCategory) as? Category
    suspend fun getLiveArchiveChannel(api: Kord) = api.getChannel(archiveChannel) as? TextChannel
}

@Serializable
data class Configuration(val ownerId: Long = 0,
                         val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()) {
    operator fun get(id: Long?) = id?.let { guildConfigurations[it] }
    fun save() = configFile.writeText(Json.encodeToString(this))
}