package me.aberrantfox.warmbot.extensions

import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.warmbot.services.*
import java.util.WeakHashMap

private object CommandPropertyStore {
    val permissions = WeakHashMap<Command, Permission>()
}

var Command.requiredPermissionLevel: Permission
    get() = CommandPropertyStore.permissions[this] ?: DEFAULT_REQUIRED_PERMISSION
    set(value) {
        CommandPropertyStore.permissions[this] = value
    }