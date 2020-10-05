package me.jakejmattson.modmail.extensions

import me.jakejmattson.discordkt.api.dsl.*
import me.jakejmattson.modmail.services.*
import kotlin.collections.set

val categoryPermissions: MutableMap<String, Permission> = mutableMapOf()
val commandPermissions: MutableMap<Command, Permission> = mutableMapOf()

var CommandSetBuilder.requiredPermissionLevel
    get() = categoryPermissions[category] ?: DEFAULT_REQUIRED_PERMISSION
    set(value) {
        categoryPermissions[category] = value
    }

var Command.requiredPermissionLevel: Permission
    get() {
        val setLevel = categoryPermissions.toList()
            .firstOrNull { category in it.first }?.second

        val cmdLevel = commandPermissions[this]

        if (cmdLevel != null) return cmdLevel
        if (setLevel != null) return setLevel
        return DEFAULT_REQUIRED_PERMISSION
    }
    set(value) {
        commandPermissions[this] = value
    }