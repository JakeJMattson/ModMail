package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.warmbot.extensions.*
import net.dv8tion.jda.core.Permission

@Service
class ConfigInitializer(configuration: Configuration, jdaInitializer: JdaInitializer) {
    init {
        configuration.guildConfigurations.forEach { addOverrides(it) }
    }

    private fun addOverrides(config: GuildConfiguration) {
        val staffRole = config.staffRoleName.nameToRole()
        val reportCategory = config.reportCategory.idToCategory()
        val archiveChannel = config.archiveChannel.idToTextChannel()

        reportCategory.putPermissionOverride(staffRole).setAllow(Permission.MESSAGE_READ).queue()
        reportCategory.putPermissionOverride(reportCategory.guild.publicRole).setDeny(Permission.MESSAGE_READ).queue()

        archiveChannel.putPermissionOverride(staffRole).setAllow(Permission.MESSAGE_READ).queue()
        archiveChannel.putPermissionOverride(reportCategory.guild.publicRole).setDeny(Permission.MESSAGE_READ).queue()
    }
}