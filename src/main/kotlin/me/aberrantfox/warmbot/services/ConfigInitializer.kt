package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.Permission

@Service
class ConfigInitializer(jda: JDA, configuration: Configuration)
{
    init {
        configuration.guildConfigurations.forEach { addOverrides(jda, it) }
    }

    private fun addOverrides(jda: JDA, config: GuildConfiguration) {
        val staffRole = jda.getRolesByName(config.staffRoleName, true).first()
        val reportCategory = jda.getCategoryById(config.reportCategory)
        val archiveChannel = jda.getTextChannelById(config.archiveChannel)

        reportCategory.putPermissionOverride(staffRole).setAllow(Permission.MESSAGE_READ).queue()
        reportCategory.putPermissionOverride(reportCategory.guild.publicRole).setDeny(Permission.MESSAGE_READ).queue()

        archiveChannel.putPermissionOverride(staffRole).setAllow(Permission.MESSAGE_READ).queue()
        archiveChannel.putPermissionOverride(reportCategory.guild.publicRole).setDeny(Permission.MESSAGE_READ).queue()
    }
}