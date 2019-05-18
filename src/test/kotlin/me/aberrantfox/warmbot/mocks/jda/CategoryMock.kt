package me.aberrantfox.warmbot.mocks.jda

import io.mockk.every
import io.mockk.mockk
import me.aberrantfox.warmbot.mocks.TestConstants
import net.dv8tion.jda.core.entities.Category
import net.dv8tion.jda.core.entities.Guild

fun produceCategoryMock(_guild: Guild) = mockk<Category>(relaxed = true) {
    every { guild } returns _guild
    every { id } returns TestConstants.Category_ID
}