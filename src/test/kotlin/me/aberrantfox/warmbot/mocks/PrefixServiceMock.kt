package me.aberrantfox.warmbot.mocks

import io.mockk.mockk
import me.aberrantfox.warmbot.services.PrefixService

val prefixServiceMock = mockk<PrefixService>(relaxed = true)