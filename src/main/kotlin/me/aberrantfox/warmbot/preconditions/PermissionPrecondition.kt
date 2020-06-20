package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.warmbot.extensions.requiredPermissionLevel
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.annotations.Precondition
import me.jakejmattson.kutils.api.dsl.preconditions.*
import me.jakejmattson.kutils.api.extensions.jda.toMember

@Precondition
fun produceHasPermissionPrecondition(permissionsService: PermissionsService) = precondition {
    val requiredPermissionLevel = it.command?.requiredPermissionLevel ?: DEFAULT_REQUIRED_PERMISSION
    val guild = it.guild!!
    val member = it.author.toMember(guild)!!

    if (!permissionsService.hasClearance(member, requiredPermissionLevel))
        return@precondition Fail(Locale.FAIL_MISSING_CLEARANCE)

    return@precondition Pass
}