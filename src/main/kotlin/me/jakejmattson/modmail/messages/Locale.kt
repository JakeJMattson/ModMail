package me.jakejmattson.modmail.messages

import com.google.gson.GsonBuilder
import me.jakejmattson.modmail.services.messagesFile
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.*

private val gson = GsonBuilder().setPrettyPrinting().create()

val Locale: Messages = load()

private fun load() = updateMessages(
    if (!messagesFile.exists())
        gson.toJson(Messages()).apply { messagesFile.writeText(this) }
    else
        messagesFile.readText()
)

private fun updateMessages(json: String): Messages {
    messagesFile.writeText(json)
    return gson.fromJson(json, Messages::class.java)
}

infix fun String.inject(properties: Pair<String, String>) = inject(mapOf(properties))

infix fun String.inject(properties: Map<String, String>): String {
    val context = VelocityContext().apply { properties.forEach { put(it.key, it.value) } }
    val reader = StringReader(this)

    return StringWriter().apply {
        VelocityEngine().evaluate(context, this, "template", reader)
    }.toString()
}