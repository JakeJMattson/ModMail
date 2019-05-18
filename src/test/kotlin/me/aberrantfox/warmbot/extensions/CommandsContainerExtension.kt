package me.aberrantfox.warmbot.extensions

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.api.dsl.CommandsContainer

fun CommandsContainer.executeByName(name: String, event: CommandEvent) = this[name]!!.execute(event)