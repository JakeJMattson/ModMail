package me.jakejmattson.modmail.extensions

import me.jakejmattson.discordkt.api.dsl.command.*
import me.jakejmattson.modmail.services.*

val categoryPermissions: MutableMap<CommandsContainer, Permission> = mutableMapOf()
val commandPermissions: MutableMap<Command, Permission> = mutableMapOf()

var CommandsContainer.requiredPermissionLevel
    get() = categoryPermissions[this] ?: DEFAULT_REQUIRED_PERMISSION
    set(value) {
        categoryPermissions[this] = value
    }

var Command.requiredPermissionLevel: Permission
    get() {
        val setLevel = categoryPermissions.toList()
            .firstOrNull { this in it.first.commands }?.second

        val cmdLevel = commandPermissions[this]

        if (cmdLevel != null) return cmdLevel
        if (setLevel != null) return setLevel
        return DEFAULT_REQUIRED_PERMISSION
    }
    set(value) {
        commandPermissions[this] = value
    }