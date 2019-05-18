package me.aberrantfox.warmbot.services

import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.managers.GuildController
import java.util.Vector

private val detainedReports = Vector<Report>()

fun Report.detain() {
    val member = reportToMember()

    if (!member.isDetained())
        detainedReports.addElement(this)
}

fun Report.release(): Boolean {
    val member = this.reportToMember()

    member.unmute()

    if (member.isDetained())
        detainedReports.remove(this)

    return true
}

fun Member.isDetained() = detainedReports.any { it.userId == this.user.id}

fun Member.mute(): Boolean {
    val mutedRole = guild.getRolesByName("Muted", true).firstOrNull() ?: return false

    if (!this.roles.contains(mutedRole))
        GuildController(guild).addSingleRoleToMember(this, mutedRole).queue()

    return true
}

fun Member.unmute(): Boolean{
    val mutedRole = guild.getRolesByName("Muted", true).firstOrNull() ?: return false

    if (roles.contains(mutedRole))
        GuildController(guild).removeSingleRoleFromMember(this, mutedRole).queue()

    return true
}