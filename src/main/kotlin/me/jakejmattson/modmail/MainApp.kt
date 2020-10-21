package me.jakejmattson.modmail

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.extensions.requiredPermissionLevel
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
import java.awt.Color
import kotlin.system.exitProcess

@Serializable
private data class Properties(val version: String, val kotlin: String, val repository: String)

private val propFile = Properties::class.java.getResource("/properties.json").readText()
private val project = Json.decodeFromString<Properties>(propFile)

suspend fun main(it: Array<String>) {
    val token = it.firstOrNull()

    if (token == null || token == "UNSET") {
        println("You must specify the token with the -e flag when running via docker, or as the first command line param.")
        exitProcess(-1)
    }

    bot(token) {
        prefix {
            val configuration = discord.getInjectionObjects(Configuration::class)
            guild?.let { configuration[it.id.longValue]?.prefix.takeUnless { it.isNullOrBlank() } ?: "!" } ?: "<none>"
        }

        configure {
            theme = Color(0x00bfff)
        }

        mentionEmbed {
            val self = api.getSelf()
            val guild = it.guild
            val configuration = it.discord.getInjectionObjects(Configuration::class)
            val staffId = configuration[guild?.id?.longValue]?.staffRoleId

            val requiredRole = if (guild != null)
                staffId?.let { guild.getRole(it.toSnowflake()) }?.mention ?: "<Not Configured>"
            else
                "<Not Applicable>"

            title = "ModMail ${project.version}"
            description = "A Discord report management bot."

            thumbnail {
                url = self.avatar.url
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