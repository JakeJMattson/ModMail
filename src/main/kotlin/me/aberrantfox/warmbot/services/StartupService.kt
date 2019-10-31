package me.aberrantfox.warmbot.services

import com.google.gson.Gson
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.command.Command
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.extensions.jda.*
import me.aberrantfox.warmbot.extensions.*
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import java.awt.Color

@Service
class StartupService(configuration: Configuration,
                     discord: Discord,
                     permissionsService: PermissionsService,
                     jdaInitializer: JdaInitializer) {
    private data class Properties(val version: String, val kutils: String, val repository: String)
    private val propFile = Properties::class.java.getResource("/properties.json").readText()
    private val project = Gson().fromJson(propFile, Properties::class.java)

    init {
        with(discord.configuration) {
            prefix = configuration.prefix

            mentionEmbed = {
                embed {
                    val channel = it.channel
                    val self = channel.jda.selfUser
                    val requiredRole = configuration.getGuildConfig(channel.guild.id)?.staffRoleName ?: "<Not Configured>"

                    color = Color(0x00bfff)
                    thumbnail = self.effectiveAvatarUrl
                    addField(self.fullName(), "A Discord report management bot.")
                    addInlineField("Required role", requiredRole)
                    addInlineField("Prefix", configuration.prefix)
                    addInlineField("Contributors", "Fox#0001, Elliott#0001, JakeyWakey#1569")

                    with (project) {
                        val kotlinVersion = KotlinVersion.CURRENT

                        addField("Build Info", "```" +
                            "Version: $version\n" +
                            "KUtils: $kutils\n" +
                            "Kotlin: $kotlinVersion" +
                            "```")

                        addField("Source", repository)
                    }
                }
            }

            visibilityPredicate = predicate@{ command: Command, user: User, _: MessageChannel, guild: Guild? ->
                guild ?: return@predicate false

                val member = user.toMember(guild)!!
                val permission = command.requiredPermissionLevel

                permissionsService.hasClearance(member, permission)
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