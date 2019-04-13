package me.aberrantfox.warmbot.services

import io.mockk.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.mocks.*
import me.aberrantfox.warmbot.mocks.jda.guildMock
import org.junit.jupiter.api.*

class MacroServiceTest {
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

    private lateinit var macroService: MacroService
    private lateinit var mockEmptyMacro: Macro
    private val mockTestMacro = Macro(TestConstants.Macro_Name, TestConstants.Macro_Message)

    @BeforeEach
    fun setup() {
        macroService = MacroService()
        mockEmptyMacro = Macro(name = "", message = "")
        macroMap.map.clear()
    }

    @Test
    fun `add macro to empty map`() {
        verifyEmptyMap()
        macroService.addMacro(TestConstants.Macro_Name, TestConstants.Macro_Message, guildMock)
        verifyTestMacroInMap()
    }

    @Test
    fun `add macro to populated map`() {
        populateMap(mockTestMacro)
        macroService.addMacro(TestConstants.Macro_Name, TestConstants.Macro_Message, guildMock)
        verifyTestMacroInMap()
    }

    @Test
    fun `remove macro from empty map`() {
        verifyEmptyMap()
        macroService.removeMacro(mockTestMacro, guildMock)
        verifyEmptyMap()
    }

    @Test
    fun `remove macro from populated map`() {
        populateMap(mockTestMacro)
        macroService.removeMacro(mockTestMacro, guildMock)
        verifyEmptyMap()
    }

    @Test
    fun `edit the name of a macro`() {
        populateMap(mockEmptyMacro)
        Assertions.assertEquals("", getGuildMacros(TestConstants.Guild_ID).first().name)
        macroService.editName(mockEmptyMacro, TestConstants.Macro_Name, guildMock)
        Assertions.assertEquals(TestConstants.Macro_Name, getGuildMacros(TestConstants.Guild_ID).first().name)
    }

    @Test
    fun `edit the content of a macro`() {
        populateMap(mockEmptyMacro)
        Assertions.assertEquals("", getGuildMacros(TestConstants.Guild_ID).first().message)
        macroService.editMessage(mockEmptyMacro, TestConstants.Macro_Message)
        Assertions.assertEquals(TestConstants.Macro_Message, getGuildMacros(TestConstants.Guild_ID).first().message)
    }

    private fun populateMap(macro: Macro) {
        verifyEmptyMap()
        macroMap.map[TestConstants.Guild_ID] = arrayListOf(macro)
        Assertions.assertEquals(arrayListOf(macro), getGuildMacros(TestConstants.Guild_ID))
    }

    private fun verifyEmptyMap() = Assertions.assertTrue(getGuildMacros(TestConstants.Guild_ID).isEmpty())

    private fun verifyTestMacroInMap() {
        val macros = getGuildMacros(TestConstants.Guild_ID)
        Assertions.assertTrue(macros.size == 1)
        Assertions.assertTrue(macros.first() == mockTestMacro)
    }
}