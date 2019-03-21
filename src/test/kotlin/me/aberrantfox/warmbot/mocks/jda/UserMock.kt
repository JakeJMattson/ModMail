package me.aberrantfox.warmbot.mocks.jda

import io.mockk.*
import me.aberrantfox.warmbot.mocks.TestConstants
import net.dv8tion.jda.core.entities.User

fun produceUserMock() = mockk<User>(relaxed = true) {
    every { id } returns TestConstants.User_ID
}