package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.services.isReportChannel

private const val Category = "Report"

@Precondition
fun produceIsReportPrecondition() = precondition {
    val command = it.container[it.commandStruct.commandName] ?: return@precondition Pass

    if (command.category != Category) return@precondition Pass

    if (!it.channel.isReportChannel()) return@precondition Fail("This command must be invoked inside a report.")

    return@precondition Pass
}

