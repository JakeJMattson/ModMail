package me.aberrantfox.warmbot

import com.google.gson.Gson
import me.jakejmattson.kutils.api.dsl.configuration.startBot
import me.jakejmattson.kutils.api.extensions.jda.*
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import java.awt.Color
import kotlin.system.exitProcess

private data class Properties(val version: String, val kutils: String, val repository: String)

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

            prefix { configuration.prefix }

            colors {
                infoColor = Color(0x00bfff)
            }

            mentionEmbed {
                val self = discord.jda.selfUser
                val guild = it.guild

                val requiredRole = if (guild != null)
                    configuration.getGuildConfig(guild.id)?.staffRoleName ?: "<Not Configured>"
                else
                    "<Not Applicable>"

                title = "${self.fullName()} (WarmBot ${project.version})"
                description = "A Discord report management bot."
                thumbnail = self.effectiveAvatarUrl
                color = infoColor

                addField("Contributors", "Fox#0001, Elliott#0001, JakeyWakey#1569")
                addInlineField("Required role", requiredRole)
                addInlineField("Prefix", configuration.prefix)
                addInlineField("Build Info", "`${discord.properties.kutilsVersion} - ${discord.properties.jdaVersion}`")
                addInlineField("Source", project.repository)
            }

            visibilityPredicate {
                val guild = it.guild ?: return@visibilityPredicate false

                val member = it.user.toMember(guild)!!
                val permission = it.command.requiredPermissionLevel

                permissionsService.hasClearance(member, permission)
            }

            configuration.guildConfigurations.forEach { addOverrides(it) }
        }

        discord.jda.presence.activity = Activity.playing(Locale.DEFAULT_DISCORD_PRESENCE)
    }
}

private fun addOverrides(config: GuildConfiguration) {
    val staffRole = config.guildId.idToGuild()?.getRolesByName(config.staffRoleName, true)?.firstOrNull() ?: return
    val reportCategory = config.reportCategory.idToCategory() ?: return
    val archiveChannel = config.archiveChannel.idToTextChannel() ?: return

    reportCategory.putPermissionOverride(staffRole).setAllow(Permission.MESSAGE_READ).queue()
    reportCategory.putPermissionOverride(reportCategory.guild.publicRole).setDeny(Permission.MESSAGE_READ).queue()

    archiveChannel.putPermissionOverride(staffRole).setAllow(Permission.MESSAGE_READ).queue()
    archiveChannel.putPermissionOverride(reportCategory.guild.publicRole).setDeny(Permission.MESSAGE_READ).queue()
}