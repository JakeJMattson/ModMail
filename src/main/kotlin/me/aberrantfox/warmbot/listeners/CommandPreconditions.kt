package me.aberrantfox.warmbot.listeners

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.warmbot.ObjectRegister
import me.aberrantfox.warmbot.services.Configuration

val isStaffMember = { event: CommandEvent ->
    val config = ObjectRegister["config"] as Configuration
    val staffRole = event.jda.getRolesByName(config.staffRoleName, true).first()
    val relevantGuild = event.jda.guilds.first()
    val memberAuthor = relevantGuild.getMember(event.author)

    memberAuthor.roles.contains(staffRole)
}