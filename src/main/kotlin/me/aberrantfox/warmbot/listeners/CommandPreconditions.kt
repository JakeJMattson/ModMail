package me.aberrantfox.warmbot.listeners

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.internal.command.Fail
import me.aberrantfox.kjdautils.internal.command.Pass

fun produceIsStaffMemberPrecondition(staffRoleName: String, guildId: String) = { event: CommandEvent ->

    val relevantGuild = event.jda.guilds.first {g -> g.id == guildId}
    val staffRole = relevantGuild.getRolesByName(staffRoleName, true).first()
    val memberAuthor = relevantGuild.getMember(event.author)

    if(memberAuthor.roles.contains(staffRole)) {
        Pass
    } else {
        Fail("You do not have the staff role.")
    }
}