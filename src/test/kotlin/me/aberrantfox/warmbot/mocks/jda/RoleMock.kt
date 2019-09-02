package me.aberrantfox.warmbot.mocks.jda

import io.mockk.*
import net.dv8tion.jda.api.entities.Role

fun produceRoleMock(_name: String) = mockk<Role>(relaxed = true) {
    every { name } returns _name
}