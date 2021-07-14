package me.jakejmattson.modmail.services

import dev.kord.core.any
import me.jakejmattson.discordkt.api.dsl.PermissionContext
import me.jakejmattson.discordkt.api.dsl.PermissionSet

enum class Permission : PermissionSet {
    BOT_OWNER {
        override suspend fun hasPermission(context: PermissionContext): Boolean {
            return context.discord.getInjectionObjects<Configuration>().ownerId == context.user.id
        }
    },
    GUILD_OWNER {
        override suspend fun hasPermission(context: PermissionContext) = context.getMember()?.isOwner() ?: false
    },
    STAFF {
        override suspend fun hasPermission(context: PermissionContext): Boolean {
            val guild = context.guild ?: return false
            val member = context.getMember()!!
            val configuration = context.discord.getInjectionObjects<Configuration>()
            return member.roles.any { it.id == configuration[guild.id]?.staffRoleId }
        }
    }
}