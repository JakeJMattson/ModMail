package me.aberrantfox.warmbot.listeners

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.internal.command.Fail
import me.aberrantfox.kjdautils.internal.command.Pass
import me.aberrantfox.warmbot.services.GuildConfiguration
import net.dv8tion.jda.core.entities.TextChannel

fun produceIsStaffMemberPrecondition(guildConfigurations: List<GuildConfiguration>) = { event: CommandEvent ->

    val textChannel = event.channel as TextChannel
    val relevantGuildConfiguration = guildConfigurations.first { g -> g.guildId == textChannel.guild.id }
    val relevantGuild = event.jda.getGuildById(relevantGuildConfiguration.guildId)
    val staffRole = relevantGuild.getRolesByName(relevantGuildConfiguration.staffRoleName, true).first()
    val memberAuthor = relevantGuild.getMember(event.author)

    if (memberAuthor.roles.contains(staffRole)) {
        Pass
    } else {
        Fail("You do not have the staff role.")
    }
}