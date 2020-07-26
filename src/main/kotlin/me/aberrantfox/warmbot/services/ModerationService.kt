package me.aberrantfox.warmbot.services

import me.jakejmattson.kutils.api.annotations.Service
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import java.util.Vector

private val detainedReports = Vector<Report>()

@Service
class ModerationService(val configuration: Configuration) {
    fun hasStaffRole(member: Member): Boolean {
        val jda = member.jda
        val guild = member.guild
        val staffRoleName = configuration.getGuildConfig(guild.id)?.staffRoleName ?: return false
        val staffRole = jda.getRolesByName(staffRoleName, true).firstOrNull() ?: return false

        return staffRole in member.roles
    }
}

fun Report.detain(jda: JDA) {
    val member = toLiveReport(jda)?.member ?: return

    if (!member.isDetained())
        detainedReports.addElement(this)
}

fun Report.release(jda: JDA): Boolean {
    val member = toLiveReport(jda)?.member ?: return false

    if (member.isDetained()) {
        detainedReports.remove(this)
        member.unmute()
    }

    return true
}

fun Member.isDetained() = detainedReports.any { it.userId == this.user.id }

fun Member.mute(): Boolean {
    val mutedRole = guild.getRolesByName("Muted", true).firstOrNull() ?: return false

    if (!this.roles.contains(mutedRole))
        guild.modifyMemberRoles(this, roles + mutedRole).queue()

    return true
}

fun Member.unmute(): Boolean {
    val mutedRole = guild.getRolesByName("Muted", true).firstOrNull() ?: return false

    if (roles.contains(mutedRole))
        guild.modifyMemberRoles(this, roles - mutedRole).queue()

    return true
}