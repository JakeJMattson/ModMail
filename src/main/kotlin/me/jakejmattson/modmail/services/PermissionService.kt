package me.jakejmattson.modmail.services

import me.jakejmattson.discordkt.api.annotations.Service
import net.dv8tion.jda.api.entities.Member

enum class Permission {
    BOT_OWNER,
    GUILD_OWNER,
    STAFF,
    NONE
}

val DEFAULT_REQUIRED_PERMISSION = Permission.STAFF

@Service
class PermissionsService(private val configuration: Configuration) {
    fun hasClearance(member: Member, requiredPermissionLevel: Permission) = member.getPermissionLevel().ordinal <= requiredPermissionLevel.ordinal

    private fun Member.getPermissionLevel() =
        when {
            isBotOwner() -> Permission.BOT_OWNER
            isGuildOwner() -> Permission.GUILD_OWNER
            isStaff() -> Permission.STAFF
            else -> Permission.NONE
        }

    private fun Member.isBotOwner() = user.idLong == configuration.ownerId
    private fun Member.isGuildOwner() = isOwner
    private fun Member.isStaff() = configuration[guild.idLong]?.getLiveRole(guild.jda) in roles
}