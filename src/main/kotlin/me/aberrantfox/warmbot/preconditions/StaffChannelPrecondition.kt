package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.internal.command.Fail
import me.aberrantfox.kjdautils.internal.command.Pass
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.TextChannel

fun produceIsStaffChannelPrecondition(configuration: Configuration) = { event: CommandEvent ->

    if (event.channel is TextChannel) {
        val textChannel = event.channel as TextChannel

        if (configuration.hasGuildConfig(textChannel.guild.id)) {
            val relevantGuildConfiguration = configuration.getGuildConfig(textChannel.guild.id)!!
            val relevantGuild = event.jda.getGuildById(textChannel.guild.id)
            val staffRole = relevantGuild.getRolesByName(relevantGuildConfiguration.staffRoleName, true).first()
            val perms = textChannel.getPermissionOverride(staffRole)

            if (perms == null) Fail() else Pass
        }
        else Pass
    }
    else Fail()
}