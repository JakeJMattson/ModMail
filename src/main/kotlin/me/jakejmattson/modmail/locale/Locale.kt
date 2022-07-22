package me.jakejmattson.modmail.locale

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.jakejmattson.modmail.services.messagesFile
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.StringReader
import java.io.StringWriter

val Locale = load()

private fun load() = updateMessages(
    if (!messagesFile.exists())
        Json.encodeToString(Messages()).apply { messagesFile.writeText(this) }
    else
        messagesFile.readText()
)

private fun updateMessages(json: String): Messages {
    messagesFile.writeText(json)
    return Json.decodeFromString(json)
}

infix fun String.inject(properties: Pair<String, String>) = inject(mapOf(properties))

infix fun String.inject(properties: Map<String, String>): String {
    val context = VelocityContext().apply { properties.forEach { put(it.key, it.value) } }
    val reader = StringReader(this)

    return StringWriter().apply {
        VelocityEngine().evaluate(context, this, "template", reader)
    }.toString()
}