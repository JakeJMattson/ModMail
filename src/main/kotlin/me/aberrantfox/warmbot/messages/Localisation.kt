package me.aberrantfox.warmbot.messages

import com.google.gson.GsonBuilder
import java.io.File

private const val resourcePath = "/default-messages.json"
private val filePath = "config${File.separatorChar}messages.json"

private val gson = GsonBuilder().setPrettyPrinting().create()
private val localFile = File(filePath)

object Localisation {
    lateinit var messages: Messages

    init { load() }

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