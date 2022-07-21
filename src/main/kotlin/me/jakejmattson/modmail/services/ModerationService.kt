package me.jakejmattson.modmail.services

import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Member
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.annotations.Service
import java.util.*

private val detainedReports = Vector<Report>()

@Service
class ModerationService(val configuration: Configuration) {
    suspend fun hasStaffRole(member: Member): Boolean {
        val guild = member.getGuild()
        val staffRoleId = configuration[guild]?.staffRoleId ?: return false

        return member.roles.firstOrNull { it.id == staffRoleId } != null
    }
}

suspend fun Report.detain(kord: Kord) {
    val member = toLiveReport(kord)?.user?.asMember(guildId) ?: return

    if (!member.isDetained()) {
        detainedReports.addElement(this)
        member.mute()
    }
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