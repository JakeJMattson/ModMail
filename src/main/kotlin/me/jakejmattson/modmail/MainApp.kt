package me.jakejmattson.modmail

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.kColor
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.extensions.*
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.Locale
import me.jakejmattson.modmail.services.configFile
import java.awt.Color
import java.time.Instant
import kotlin.system.exitProcess

@Serializable
private data class Properties(val version: String, val kotlin: String, val repository: String) : Data()

private val startup = Instant.now()

@KordPreview
@PrivilegedIntent
suspend fun main(it: Array<String>) {
    val token = it.firstOrNull()

    if (token == null || token == "UNSET") {
        println("You must specify the token with the -e flag when running via docker, or as the first command line param.")
        exitProcess(-1)
    }

    bot(token) {
        val configuration = data(configFile.path) { Configuration() }
        val project = data(javaClass.getResource("/properties.json")?.path ?: "") { Properties("0.0", "0.0", "0.0") }

        prefix {
            guild?.let { configuration[it]?.prefix } ?: " "
        }

        configure {
            commandReaction = null
            dualRegistry = false
            recommendCommands = false
            intents = Intent.Guilds + Intent.GuildMembers + Intent.GuildBans + Intent.DirectMessages + Intent.DirectMessageTyping + Intent.GuildMessageTyping
            defaultPermissions = Permissions(Permission.ManageMessages)
        }

        mentionEmbed {
            title = "ModMail ${project.version}"
            description = "A Discord report management bot."
            color = Color.white.kColor
            thumbnail(it.discord.kord.getSelf().pfpUrl)
            addInlineField("Source", "[GitHub](${project.repository})")
            addInlineField("Ping", it.discord.kord.gateway.averagePing?.toString() ?: "Unknown")
            addInlineField("Startup", TimeStamp.at(startup, TimeStyle.RELATIVE))
            footer(it.discord.versions.toString())
        }

        presence {
            playing(Locale.DISCORD_PRESENCE)
        }
    }
}