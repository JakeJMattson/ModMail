package me.jakejmattson.modmail

import dev.kord.common.annotation.KordPreview
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.addInlineField
import me.jakejmattson.modmail.extensions.requiredPermissionLevel
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.PermissionsService
import me.jakejmattson.modmail.services.configFile
import java.awt.Color
import kotlin.system.exitProcess

@Serializable
private data class Properties(val version: String, val kotlin: String, val repository: String)

private val propFile = Properties::class.java.getResource("/properties.json").readText()
private val project = Json.decodeFromString<Properties>(propFile)

@OptIn(KordPreview::class)
suspend fun main(it: Array<String>) {
    val token = it.firstOrNull()

    if (token == null || token == "UNSET") {
        println("You must specify the token with the -e flag when running via docker, or as the first command line param.")
        exitProcess(-1)
    }

    bot(token) {
        val configuration = Json.decodeFromString<Configuration>(configFile.readText())
        inject(configuration)

        prefix {
            guild?.let { configuration[it.id]?.prefix.takeUnless { it.isNullOrBlank() } ?: "!" } ?: "<none>"
        }

        configure {
            commandReaction = null
            theme = Color(0x00bfff)
        }

        mentionEmbed {
            val guild = it.guild
            val staffId = configuration[guild?.id]?.staffRoleId

            val requiredRole = if (guild != null)
                staffId?.let { guild.getRole(it) }?.mention ?: "<Not Configured>"
            else
                "<Not Applicable>"

            title = "ModMail ${project.version}"
            description = "A Discord report management bot."

            thumbnail {
                url = it.discord.kord.getSelf().avatar.url
            }

            addInlineField("Prefix", it.prefix())
            addInlineField("Required Role", requiredRole)
            addInlineField("Source", "[GitHub](${project.repository})")

            footer {
                val versions = it.discord.versions
                text = "${versions.library} - ${versions.kord} - ${project.kotlin}"
            }
        }

        permissions {
            val guild = guild ?: return@permissions false
            val member = user.asMember(guild.id)
            val permission = command.requiredPermissionLevel
            val permissionsService = discord.getInjectionObjects(PermissionsService::class)

            permissionsService.hasClearance(member, permission)
        }

        presence {
            playing(Locale.DISCORD_PRESENCE)
        }
    }
}