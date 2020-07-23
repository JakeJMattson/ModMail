package me.aberrantfox.warmbot

import com.google.gson.Gson
import me.aberrantfox.warmbot.extensions.requiredPermissionLevel
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.dsl.configuration.startBot
import me.jakejmattson.kutils.api.extensions.jda.*
import net.dv8tion.jda.api.entities.Activity
import java.awt.Color
import kotlin.system.exitProcess

private data class Properties(val version: String, val repository: String)

private val propFile = Properties::class.java.getResource("/properties.json").readText()
private val project = Gson().fromJson(propFile, Properties::class.java)

fun main(args: Array<String>) {
    val token = args.firstOrNull()

    if (token == null || token == "UNSET") {
        println("You must specify the token with the -e flag when running via docker, or as the first command line param.")
        exitProcess(-1)
    }

    startBot(token) {
        configure {
            val (configuration, permissionsService) = discord.getInjectionObjects(Configuration::class, PermissionsService::class)

            requiresGuild = true

            prefix {
                if (it.guild != null) configuration.prefix else "<none>"
            }

            colors {
                infoColor = Color(0x00bfff)
            }

            mentionEmbed {
                val self = discord.jda.selfUser
                val guild = it.guild
                val guildConfig = configuration.getGuildConfig(guild?.id)

                val requiredRole = if (guild != null)
                    guildConfig?.staffRoleName ?: "<Not Configured>"
                else
                    "<Not Applicable>"

                title = "${self.fullName()} (WarmBot ${project.version})"
                description = "A Discord report management bot."
                thumbnail = self.effectiveAvatarUrl
                color = infoColor

                addField("Contributors", "Fox#0001, Elliott#0001, JakeyWakey#1569")
                addInlineField("Required role", requiredRole)
                addInlineField("Prefix", it.relevantPrefix)
                addInlineField("Build Info", "`${discord.properties.kutilsVersion} - ${discord.properties.jdaVersion}`")
                addInlineField("Source", project.repository)
            }

            visibilityPredicate {
                val guild = it.guild ?: return@visibilityPredicate false

                val member = it.user.toMember(guild)!!
                val permission = it.command.requiredPermissionLevel

                permissionsService.hasClearance(member, permission)
            }
        }

        discord.jda.presence.activity = Activity.playing(Locale.DEFAULT_DISCORD_PRESENCE)
    }
}