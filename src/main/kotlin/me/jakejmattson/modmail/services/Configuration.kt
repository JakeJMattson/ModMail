package me.jakejmattson.modmail.services

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

@Serializable
data class LoggingConfiguration(var loggingChannel: Snowflake,
                                val logEdits: Boolean = true,
                                val logCommands: Boolean = true,
                                val logOpen: Boolean = true,
                                val logClose: Boolean = true) {
    suspend fun getLiveChannel(kord: Kord) = kord.getChannel(loggingChannel) as? TextChannel
}

@Serializable
data class GuildConfiguration(var prefix: String,
                              var reportCategory: Snowflake,
                              var archiveChannel: Snowflake,
                              var staffRoleId: Snowflake,
                              val loggingConfiguration: LoggingConfiguration) {
    suspend fun getLiveReportCategory(kord: Kord) = kord.getChannel(reportCategory) as? Category
    suspend fun getLiveArchiveChannel(kord: Kord) = kord.getChannel(archiveChannel) as? TextChannel
}

@Serializable
data class Configuration(val ownerId: Snowflake = Snowflake(0),
                         val guildConfigurations: MutableMap<Snowflake, GuildConfiguration> = mutableMapOf()) : Data() {
    operator fun get(guild: Guild) = guildConfigurations[guild.id]
    //fun save() = configFile.writeText(Json.encodeToString(this))
}