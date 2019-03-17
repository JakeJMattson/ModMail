package me.aberrantfox.warmbot.mocks

import io.mockk.every
import io.mockk.mockk
import me.aberrantfox.kjdautils.api.dsl.CommandEvent

fun makeCommandEventMock(vararg _args: Any) = mockk<CommandEvent>(relaxed = true) {
    every { args } returns _args.toList()
}