package me.aberrantfox.warmbot.messages

import com.google.gson.GsonBuilder
import me.aberrantfox.warmbot.services.messagesFile
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.*

private const val resourcePath = "/default-messages.json"
private const val templateName = "Dynamic template"
private val engine = VelocityEngine()
private val gson = GsonBuilder().setPrettyPrinting().create()

val Locale: Messages = load()

private fun load() = updateMessages(
    if (messagesFile.exists()) {
        messagesFile.readText()
    } else {
        Messages::class.java.getResource(resourcePath).readText()
    }
)

private fun updateMessages(json: String): Messages {
    messagesFile.writeText(json)
    return gson.fromJson(json, Messages::class.java)
}

fun inject(message: Messages.() -> String, vararg properties: Pair<String, String>): String {
    val context = VelocityContext().apply { properties.forEach { put(it.first, it.second) } }
    val reader = StringReader(Locale.message())

    return StringWriter().apply {
        engine.evaluate(context, this, templateName, reader)
    }.toString()
}