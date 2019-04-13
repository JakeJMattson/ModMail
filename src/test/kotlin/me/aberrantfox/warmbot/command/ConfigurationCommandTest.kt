package me.aberrantfox.warmbot.command

import io.mockk.*
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.warmbot.commands.configurationCommands
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.mocks.*
import me.aberrantfox.warmbot.mocks.jda.*
import me.aberrantfox.warmbot.services.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.*

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

        @JvmStatic
        fun setterArguments() = listOf(
            Arguments.of("SetReportCategory", TestConstants.Category_ID,
                { guildConfig: GuildConfiguration -> guildConfig.reportCategory }, produceCategoryMock(guildMock)),
            Arguments.of("SetArchiveChannel", TestConstants.Channel_ID,
                { guildConfig: GuildConfiguration -> guildConfig.archiveChannel }, produceTextChannelMock(guildMock)),
            Arguments.of("SetLoggingChannel", TestConstants.Channel_ID,
                { guildConfig: GuildConfiguration -> guildConfig.loggingConfiguration.loggingChannel }, produceTextChannelMock(guildMock)),
            Arguments.of("SetStaffRole", TestConstants.Staff_Role,
                { guildConfig: GuildConfiguration -> guildConfig.staffRoleName }, TestConstants.Staff_Role)
        )
    }

    private lateinit var config: Configuration
    private lateinit var configurationCommandSet: CommandsContainer
    private val fakeChannelList_0 = arrayListOf<String>()
    private val fakeChannelList_1 = arrayListOf(TestConstants.Channel_ID)

    @BeforeEach
    fun setup() {
        config = Configuration()
        configurationCommandSet = configurationCommands(config, persistenceServiceMock)
    }

    @ParameterizedTest
    @MethodSource("setterArguments")
    fun `test setter configuration commands`(commandName: String, actual: Any, expected: (config: GuildConfiguration) -> String, eventMockArgs: Any) {
        val event = makeCommandEventMock(eventMockArgs)
        val guildConfig = config.guildConfigurations.first()

        configurationCommandSet[commandName]!!.execute(event)

        Assertions.assertEquals(actual, expected.invoke(guildConfig))
        verifyPersistenceCall()
        verifySingleResponse(event)
    }

    @Test
    fun `add staff channel to empty list`() {
        val event = makeCommandEventMock(produceTextChannelMock(guildMock))
        val guildConfig = config.guildConfigurations.first()

        configurationCommandSet["AddStaffChannel"]!!.execute(event)

        Assertions.assertEquals(guildConfig.staffChannels, fakeChannelList_1)
        verifyPersistenceCall()
        verifySingleResponse(event)
    }

    @Test
    fun `add staff channel to populated list`() {
        val event = makeCommandEventMock(produceTextChannelMock(guildMock))
        val guildConfig = config.guildConfigurations.first()
        guildConfig.staffChannels.add(TestConstants.Channel_ID)

        configurationCommandSet["AddStaffChannel"]!!.execute(event)

        Assertions.assertEquals(guildConfig.staffChannels, fakeChannelList_1)
        verifySingleResponse(event)
    }

    @Test
    fun `remove staff channel from empty list`() {
        val event = makeCommandEventMock(produceTextChannelMock(guildMock))
        val guildConfig = config.guildConfigurations.first()

        configurationCommandSet["RemoveStaffChannel"]!!.execute(event)

        Assertions.assertEquals(guildConfig.staffChannels, fakeChannelList_0)
        verifySingleResponse(event)
    }

    @Test
    fun `remove staff channel from populated list`() {
        val event = makeCommandEventMock(produceTextChannelMock(guildMock))
        val guildConfig = config.guildConfigurations.first()
        guildConfig.staffChannels.add(TestConstants.Channel_ID)

        configurationCommandSet["RemoveStaffChannel"]!!.execute(event)

        Assertions.assertEquals(guildConfig.staffChannels, fakeChannelList_0)
        verifyPersistenceCall()
        verifySingleResponse(event)
    }

    private fun verifyPersistenceCall() = verify(exactly = 1) { persistenceServiceMock.save(config) }
    private fun verifySingleResponse(event: CommandEvent) = verify(exactly = 1) { event.respond(any() as String) }
}