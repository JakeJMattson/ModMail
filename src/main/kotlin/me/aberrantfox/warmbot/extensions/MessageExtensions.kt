package me.aberrantfox.warmbot.extensions

import me.aberrantfox.kjdautils.extensions.stdlib.sanitiseMentions
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.*

const val embedNotation = "<---------- Embed ---------->"

fun Message.fullContent() = contentRaw + "\n" + attachmentsString()

fun Message.attachmentsString(): String =
        if(attachments.isNotEmpty()) attachments.map { it.url }.reduce { a, b -> "$a\n $b" } else ""

fun Message.cleanContent() = this.fullContent().trimEnd().sanitiseMentions()

fun MessageEmbed.toTextString() =
    StringBuilder().apply {
        appendln(embedNotation)
        fields.forEach { append("${it.name}\n${it.value}\n") }
        appendln(embedNotation)
    }.toString()

fun MessageEmbed.toEmbedBuilder() =
        EmbedBuilder().apply {
            setTitle(title)
            setDescription(description)
            setFooter(footer?.text, footer?.iconUrl)
            setThumbnail(thumbnail?.url)
            setTimestamp(timestamp)
            setImage(image?.url)
            setColor(colorRaw)
            setAuthor(author?.name)
            fields.addAll(this@toEmbedBuilder.fields)
        }

fun Message.addFailReaction() = this.addReaction("❌").queue()