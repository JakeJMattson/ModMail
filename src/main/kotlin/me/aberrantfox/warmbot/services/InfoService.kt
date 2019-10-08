package me.aberrantfox.warmbot.services

import com.google.gson.Gson
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.fullName
import net.dv8tion.jda.api.entities.TextChannel
import java.awt.Color

@Service
class InfoService(private val configuration: Configuration) {
    private data class Properties(val version: String, val author: String, val repository: String)
    private val propFile = Properties::class.java.getResource("/properties.json").readText()
    private val project = Gson().fromJson(propFile, Properties::class.java)

    fun botInfo(channel: TextChannel) = embed {
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