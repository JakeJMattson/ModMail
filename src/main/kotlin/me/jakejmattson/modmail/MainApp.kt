package me.jakejmattson.modmail

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.extensions.*
import me.jakejmattson.modmail.locale.Locale
import me.jakejmattson.modmail.services.*
import java.awt.Color
import java.time.Instant
import kotlin.system.exitProcess

@Serializable
private data class Properties(val version: String, val kotlin: String, val repository: String) : Data()

private val propFile = Properties::class.java.getResource("/properties.json").path

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
        val project = data(propFile) { Properties("0.0", "0.0", "0.0") }

        prefix {
            guild?.let { configuration[it]?.prefix } ?: " "
        }

        configure {
            commandReaction = null
            dualRegistry = false
            theme = Color(0x00bfff)
            intents = Intent.GuildMembers + Intent.Guilds + Intent.DirectMessages + Intent.DirectMessageTyping
            defaultPermissions = Permissions(Permission.ManageMessages)
        }

        mentionEmbed {
            val guild = it.guild ?: return@mentionEmbed
            val staffId = configuration[guild]?.staffRoleId
            val requiredRole = staffId?.let { guild.getRole(it) }?.mention ?: "<Not Configured>"

            title = "ModMail ${project.version}"
            description = "A Discord report management bot."

            thumbnail(it.discord.kord.getSelf().pfpUrl)
            addInlineField("Required Role", requiredRole)
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