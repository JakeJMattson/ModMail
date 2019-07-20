package me.aberrantfox.warmbot.messages

import com.google.gson.GsonBuilder
import me.aberrantfox.warmbot.services.*
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.*

private const val resourcePath = "/default-messages.json"

private val gson = GsonBuilder().setPrettyPrinting().create()

object Locale {
    lateinit var messages: Messages

    init {
        if(!EnvironmentSettings.IS_TESTING_ENVIRONMENT) {
            load()
        } else {
            messages = Messages()
        }
    }

    private const val templateName = "Dynamic template"
    private val engine = VelocityEngine()

    fun inject(message: Messages.() -> String, vararg properties: Pair<String, String>): String {
        val context = VelocityContext().apply { properties.forEach { put(it.first, it.second) } }
        val reader = StringReader(messages.message())

        return StringWriter().apply {
            engine.evaluate(context, this, templateName, reader)
        }.toString()
    }

    private fun load() = updateMessages(
        if (messagesFile.exists()) {
            messagesFile.readText()
        } else {
            Messages::class.java.getResource(resourcePath).readText()
        }
    )

    private fun updateMessages(json: String) {
        messages = gson.fromJson(json, Messages::class.java)
        messagesFile.writeText(json)
    }
}