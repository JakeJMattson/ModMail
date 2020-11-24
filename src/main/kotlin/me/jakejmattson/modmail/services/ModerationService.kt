package me.jakejmattson.modmail.services

import com.gitlab.kordlib.core.*
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Member
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.api.annotations.Service
import java.util.*

private val detainedReports = Vector<Report>()

@Service
class ModerationService(val configuration: Configuration) {
    suspend fun hasStaffRole(member: Member): Boolean {
        val guild = member.guild
        val staffRoleId = configuration[guild.id]?.staffRoleId ?: return false

        return member.roles.any { it.id == staffRoleId }
    }
}

suspend fun Report.detain(kord: Kord) {
    val member = toLiveReport(kord)?.user?.asMember(guildId) ?: return

    if (!member.isDetained())
        detainedReports.addElement(this)
}

suspend fun Report.release(kord: Kord): Boolean {
    val member = toLiveReport(kord)?.user?.asMember(guildId) ?: return false

    if (member.isDetained()) {
        detainedReports.remove(this)
        member.unmute()
    }

    return true
}

fun Member.isDetained() = detainedReports.any { it.userId == id }

suspend fun Member.mute(): Boolean {
    val mutedRole = guild.getMutedRole() ?: return false

    if (mutedRole !in roles.toList())
        addRole(mutedRole.id)

    return true
}

suspend fun Member.unmute(): Boolean {
    val mutedRole = guild.getMutedRole() ?: return false

    if (mutedRole in roles.toList())
        removeRole(mutedRole.id)

    return true
}

private suspend fun GuildBehavior.getMutedRole() = roles.firstOrNull { it.name.equals("muted", true) }