package me.aberrantfox.warmbot.command

import io.mockk.*
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.warmbot.commands.configurationCommands
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.mocks.*
import me.aberrantfox.warmbot.mocks.jda.*
import me.aberrantfox.warmbot.services.*
import org.junit.jupiter.api.*

class ConfigurationCommandsTest {
    companion object {
        init {
            EnvironmentSettings.IS_TESTING_ENVIRONMENT = true
        }

        @BeforeAll
        fun beforeAll() {
            mockkObject(Locale) {
                every { Locale.messages } returns makeMessagesMock()
            }
        }

        @AfterAll
        fun cleanUp() {
            unmockkAll()
        }
    }

    private lateinit var config: Configuration
    private lateinit var configurationCommandSet: CommandsContainer

    @BeforeEach
    fun setup() {
        config = Configuration()
        configurationCommandSet = configurationCommands(config, persistenceServiceMock)
    }

    @Test
    fun `SetReportCategory changes the reportCategory value and saves the configuration`() {
        val event = makeCommandEventMock(produceCategoryMock(guildMock))
        configurationCommandSet["SetReportCategory"]!!.execute(event)

        Assertions.assertEquals(TestConstants.Category_ID, config.guildConfigurations.first().reportCategory)
        verifySingleResponse(event)
    }

    @Test
    fun `SetArchiveChannel changes the archiveChannel value and saves the configuration`() {
        val event = makeCommandEventMock(produceTextChannelMock(guildMock))
        configurationCommandSet["SetArchiveChannel"]!!.execute(event)

        Assertions.assertEquals(TestConstants.Channel_ID, config.guildConfigurations.first().archiveChannel)
        verifySingleResponse(event)
    }

    @Test
    fun `SetStaffRole changes the staffRoleName value and saves the configuration`() {
        val event = makeCommandEventMock(TestConstants.Staff_Role)
        configurationCommandSet["SetStaffRole"]!!.execute(event)

        Assertions.assertEquals(TestConstants.Staff_Role, config.guildConfigurations.first().staffRoleName)
        verifySingleResponse(event)
    }

    @Test
    fun `SetLoggingChannel changes the loggingChannel value and saves the configuration`() {
        val event = makeCommandEventMock(produceTextChannelMock(guildMock))
        configurationCommandSet["SetLoggingChannel"]!!.execute(event)

        Assertions.assertEquals(TestConstants.Channel_ID, config.guildConfigurations.first().loggingConfiguration.loggingChannel)
        verifySingleResponse(event)
    }

    private fun verifySingleResponse(event: CommandEvent) {
        verify(exactly = 1) {
            persistenceServiceMock.save(config)
            event.respond(any() as String)
        }
    }
}