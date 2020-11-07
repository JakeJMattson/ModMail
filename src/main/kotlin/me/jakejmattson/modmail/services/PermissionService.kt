package me.jakejmattson.modmail.services

import com.gitlab.kordlib.core.any
import com.gitlab.kordlib.core.entity.Member
import me.jakejmattson.discordkt.api.annotations.Service

enum class Permission {
    BOT_OWNER,
    GUILD_OWNER,
    STAFF,
    NONE
}

val DEFAULT_REQUIRED_PERMISSION = Permission.STAFF

@Service
class PermissionsService(private val configuration: Configuration) {
    suspend fun hasClearance(member: Member, requiredPermissionLevel: Permission) = member.getPermissionLevel().ordinal <= requiredPermissionLevel.ordinal

    private suspend fun Member.getPermissionLevel() =
        when {
            isBotOwner() -> Permission.BOT_OWNER
            isGuildOwner() -> Permission.GUILD_OWNER
            isStaff() -> Permission.STAFF
            else -> Permission.NONE
        }

    private fun Member.isBotOwner() = id.longValue == configuration.ownerId
    private suspend fun Member.isGuildOwner() = isOwner()
    private suspend fun Member.isStaff() = roles.any { it.id.longValue == configuration[guild.id.longValue]?.staffRoleId }
}