package me.aberrantfox.warmbot.extensions

import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.warmbot.services.*
import java.util.WeakHashMap

private object CommandsContainerPropertyStore {
    val permissions = WeakHashMap<CommandsContainer, Permission>()
}

var CommandsContainer.requiredPermissionLevel
    get() = CommandsContainerPropertyStore.permissions[this] ?: DEFAULT_REQUIRED_PERMISSION
    set(value) {
        CommandsContainerPropertyStore.permissions[this] = value
    }

val Command.requiredPermissionLevel: Permission
    get() = CommandsContainerPropertyStore.permissions.toList()
        .firstOrNull { this in it.first.commands }?.second ?: DEFAULT_REQUIRED_PERMISSION