package me.aberrantfox.warmbot.services

import com.google.gson.Gson
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.warmbot.extensions.*
import net.dv8tion.jda.api.Permission
import java.awt.Color

@Service
class StartupService(configuration: Configuration,
                     discord: Discord,
                     jdaInitializer: JdaInitializer) {
    private data class Properties(val version: String, val author: String, val repository: String)
    private val propFile = Properties::class.java.getResource("/properties.json").readText()
    private val project = Gson().fromJson(propFile, Properties::class.java)

    init {
        with(discord.configuration) {
            prefix = configuration.prefix

            mentionEmbed = {
                embed {
                    val channel = it.channel
                    val self = channel.jda.selfUser
                    val guildConfiguration = configuration.getGuildConfig(channel.guild.id)
                    val requiredRole = guildConfiguration?.staffRoleName ?: "<Not Configured>"
                    val staffChannels = guildConfiguration?.staffChannels ?: listOf<String>()
                    val isValidCommandChannel = if (channel.id in staffChannels) "Yes" else "No"

                    color = Color(0x00bfff)
                    thumbnail = self.effectiveAvatarUrl
                    addField(self.fullName(), "A Discord report management bot.")
                    addInlineField("Required role", requiredRole)
                    addInlineField("Prefix", configuration.prefix)
                    addInlineField("Valid Command Channel", isValidCommandChannel)
                    addInlineField("Version", project.version)
                    addInlineField("Contributors", "Fox#0001, Elliott#0001, JakeyWakey#1569")
                    addInlineField("Source", project.repository)
                }
            }
        }

        configuration.guildConfigurations.forEach { addOverrides(it) }
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
}