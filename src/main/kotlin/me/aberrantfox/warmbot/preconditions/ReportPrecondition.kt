package me.aberrantfox.warmbot.preconditions

import me.jakejmattson.kutils.api.annotations.Precondition
import me.jakejmattson.kutils.api.dsl.preconditions.*
import me.aberrantfox.warmbot.services.isReportChannel

private const val Category = "Report"

@Precondition
fun produceIsReportPrecondition() = precondition {
    val command = it.command ?: return@precondition Pass

    if (command.category != Category) return@precondition Pass

    if (!it.channel.isReportChannel()) return@precondition Fail("This command must be invoked inside a report.")

    return@precondition Pass
}

