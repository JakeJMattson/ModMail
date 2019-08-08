package me.aberrantfox.warmbot.mocks.jda

import io.mockk.*
import me.aberrantfox.warmbot.mocks.TestConstants
import net.dv8tion.jda.api.entities.*

fun produceCategoryMock(_guild: Guild) = mockk<Category>(relaxed = true) {
    every { guild } returns _guild
    every { id } returns TestConstants.Category_ID
}