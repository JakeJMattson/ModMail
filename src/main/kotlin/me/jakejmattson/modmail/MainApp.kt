package me.jakejmattson.modmail

import com.google.gson.Gson
import me.jakejmattson.kutils.api.dsl.bot
import me.jakejmattson.kutils.api.extensions.jda.*
import me.jakejmattson.modmail.extensions.requiredPermissionLevel
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
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

    bot(token) {
        configure { discord ->
            val (configuration, permissionsService) = discord.getInjectionObjects(Configuration::class, PermissionsService::class)

            requiresGuild = true

            prefix {
                it.guild?.let { configuration[it.idLong]?.prefix.takeUnless { it.isNullOrBlank() } ?: "!" } ?: "<none>"
            }

            colors {
                infoColor = Color(0x00bfff)
            }

            mentionEmbed {
                val self = discord.jda.selfUser
                val guild = it.guild
                val guildConfig = configuration[guild?.idLong]

                val requiredRole = if (guild != null)
                    guildConfig?.getLiveArchiveChannel(discord.jda)?.name ?: "<Not Configured>"
                else
                    "<Not Applicable>"

                simpleTitle = "${self.fullName()} (ModMail ${project.version})"
                description = "A Discord report management bot."
                thumbnail = self.effectiveAvatarUrl
                color = infoColor

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

            discord.jda.presence.activity = Activity.playing(Locale.DISCORD_PRESENCE)
        }
    }
}