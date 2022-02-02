package me.jakejmattson.modmail.services

import me.jakejmattson.discordkt.dsl.Permission
import me.jakejmattson.discordkt.dsl.PermissionSet
import me.jakejmattson.discordkt.dsl.permission

object Permissions : PermissionSet {
    val BOT_OWNER = permission("Bot Owner") { users(discord.getInjectionObjects<Configuration>().ownerId) }
    val GUILD_OWNER = permission("Guild Owner") { users(guild!!.ownerId) }

    val STAFF = permission("Staff") {
        discord.getInjectionObjects<Configuration>().guildConfigurations[guild?.id]?.staffRoleId?.let { roles(it) }
    }

    override val hierarchy: List<Permission> = listOf(STAFF, GUILD_OWNER, BOT_OWNER)
    override val commandDefault: Permission = STAFF
}