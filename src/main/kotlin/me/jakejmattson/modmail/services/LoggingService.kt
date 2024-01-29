package me.jakejmattson.modmail.services

import dev.kord.common.kColor
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.EmbedBuilder
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.commands.CommandEvent
import me.jakejmattson.discordkt.util.addField
import me.jakejmattson.discordkt.util.pfpUrl
import me.jakejmattson.discordkt.util.thumbnail
import java.awt.Color

@Service
class LoggingService(discord: Discord, private val config: Configuration) {
    private val kord = discord.kord

    suspend fun memberOpen(report: Report) {
        val config = config[report.guildId]!!.loggingConfiguration
        val message = "New report opened by ${report.liveMember(kord)?.tag}"

        if (config.logOpen)
            log(config, message)
    }

    suspend fun staffOpen(guild: Guild, channelName: String, staff: User, detain: Boolean) {
        val config = guild.logConfig
        val message = "Staff action :: ${staff.tag} ${if (detain) "detained" else "opened"} $channelName"

        if (config.logOpen)
            log(config, message)
    }

    suspend fun archive(guild: Guild, channelName: String, staff: User) {
        val config = guild.logConfig
        val message = "Staff action :: ${staff.tag} archived $channelName"

        if (config.logClose)
            log(config, message)
    }

    suspend fun commandClose(guild: Guild, channelName: String, staff: User) {
        val config = guild.logConfig
        val message = "Staff action :: ${staff.tag} closed $channelName"

        if (config.logClose)
            log(config, message)
    }

    suspend fun manualClose(guild: Guild, channelName: String) {
        val config = guild.logConfig
        val message = "Staff action :: $channelName was deleted. See the server audit log for more information."

        if (config.logClose)
            log(config, message)
    }

    suspend fun edit(report: Report, old: String, new: String) {
        val config = config[report.guildId]!!.loggingConfiguration

        if (config.logEdits)
            logEmbed(config, buildEditEmbed(report, old, new))
    }

    suspend fun command(command: CommandEvent<*>, additionalInfo: String = "") = command.guild!!.logConfig.apply {
        val author = command.author.tag
        val commandName = command.command?.name
        val channelName = (command.channel as TextChannel).name

        if (logCommands)
            log(this, "$author invoked `${commandName}` in ${channelName}. $additionalInfo")
    }

    private val Guild.logConfig
        get() = config[this]!!.loggingConfiguration

    private suspend fun log(config: LoggingConfiguration, message: String) = config.getLiveChannel(kord)?.createMessage(message)
    private suspend fun logEmbed(config: LoggingConfiguration, embed: EmbedBuilder) = config.getLiveChannel(kord)?.createMessage {
        embeds = mutableListOf(embed)
    }

    private suspend fun buildEditEmbed(report: Report, old: String, new: String) = EmbedBuilder().apply {
        fun createFields(title: String, message: String) = message.chunked(1024).mapIndexed { index, chunk ->
            field {
                name = if (index == 0) title else "(cont)"
                value = chunk
                inline = false
            }
        }

        color = Color.white.kColor
        thumbnail(report.liveMember(kord)?.pfpUrl ?: "")
        addField("Edit Detected!", "<@!${report.userId}> edited a message.")
        createFields("Old Content", old)
        createFields("New Content", new)
    }
}