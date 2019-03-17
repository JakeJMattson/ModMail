package me.aberrantfox.warmbot.mocks

import io.mockk.every
import io.mockk.mockk
import me.aberrantfox.kjdautils.internal.di.PersistenceService

val persistenceServiceMock = mockk<PersistenceService>(relaxed = true)