package me.aberrantfox.warmbot.messages

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.StringReader
import java.io.StringWriter

class Messages (
    val SET_REPORT_CATEGORY_DESCRIPTION: String,
    val REPORT_ARCHIVE_SUCCESSFUL: String
)

private const val templateName = "Dynamic template"
private val engine = VelocityEngine()

fun Messages.dynMessage(message: Messages.() -> String, properties: HashMap<String, String>): String {
    val _message = message()
    val context = VelocityContext().apply { properties.forEach { k, v -> put(k, v) } }
    val reader = StringReader(_message)
    val writer = StringWriter()

    engine.evaluate(context, writer, templateName, reader)

    return writer.toString()
}
