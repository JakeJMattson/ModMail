package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.toMember
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.extensions.requiredPermissionLevel
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*

@Precondition
fun produceHasPermissionPrecondition(permissionsService: PermissionsService) = precondition {
    val command = it.container[it.commandStruct.commandName]
    val requiredPermissionLevel = command?.requiredPermissionLevel ?: DEFAULT_REQUIRED_PERMISSION
    val guild = it.guild!!
    val member = it.author.toMember(guild)!!

    if (!permissionsService.hasClearance(member, requiredPermissionLevel))
        return@precondition Fail(Locale.FAIL_MISSING_CLEARANCE)

    return@precondition Pass
}