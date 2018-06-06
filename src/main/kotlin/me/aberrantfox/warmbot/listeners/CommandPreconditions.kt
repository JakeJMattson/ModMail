package me.aberrantfox.warmbot.listeners

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.internal.command.Fail
import me.aberrantfox.kjdautils.internal.command.Pass

fun produceIsStaffMemberPrecondition(staffRoleName: String) = { event: CommandEvent ->
    val staffRole = event.jda.getRolesByName(staffRoleName, true).first()
    val relevantGuild = event.jda.guilds.first()
    val memberAuthor = relevantGuild.getMember(event.author)

    if(memberAuthor.roles.contains(staffRole)) {
        Pass
    } else {
        Fail("You do not have the staff role.")
    }
}