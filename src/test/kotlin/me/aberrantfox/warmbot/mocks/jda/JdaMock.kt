package me.aberrantfox.warmbot.mocks.jda

import io.mockk.*
import net.dv8tion.jda.api.JDA

val jdaMock = mockk<JDA>(relaxed = true){
    every { getRolesByName(any() as String, any() as Boolean) } returns arrayListOf(produceRoleMock("staff-name"))
}