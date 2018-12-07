package me.aberrantfox.warmbot.messages

import com.google.gson.GsonBuilder
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.File
import java.io.StringReader
import java.io.StringWriter

private const val resourcePath = "/default-messages.json"
private val filePath = "config${File.separatorChar}messages.json"

private val gson = GsonBuilder().setPrettyPrinting().create()
private val localFile = File(filePath)

object Locale {
    lateinit var messages: Messages

    init { load() }

    private const val templateName = "Dynamic template"
    private val engine = VelocityEngine()

    fun inject(message: Messages.() -> String, vararg properties: Pair<String, String>): String {
        val _message = messages.message()
        val context = VelocityContext().apply { properties.forEach { put(it.first, it.second) } }
        val reader = StringReader(_message)
        val writer = StringWriter()

        engine.evaluate(context, writer, templateName, reader)

        return writer.toString()
    }

    private fun load() =
        if (localFile.exists()) {
            updateMessages(localFile.readText())
        } else {
            updateMessages(Messages::class.java.getResource(resourcePath).readText())
        }

    private fun updateMessages(json: String) {
        messages = gson.fromJson(json, Messages::class.java)
        localFile.writeText(json)
    }
}