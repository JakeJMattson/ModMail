package me.aberrantfox.warmbot.mocks

import io.mockk.every
import io.mockk.mockk
import me.aberrantfox.warmbot.messages.Messages


fun makeMessagesMock(): Messages {
    val mock = mockk<Messages> {
        every { SET_REPORT_CATEGORY_DESCRIPTION } returns ""
    }

    //Prevent any loading or saving to the config file.
    every { mock["updateMessages"]() } returns Unit
    every { mock["load"]() } returns Unit

    return mock
}