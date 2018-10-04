package me.aberrantfox.warmbot.extensions

import me.aberrantfox.kjdautils.extensions.jda.fullName
import net.dv8tion.jda.core.entities.MessageChannel

fun MessageChannel.archiveString(prefix: String): String {

	val newline = System.lineSeparator()
	val embedNotation = "<---------- Embed ---------->"

	return iterableHistory.reversed().filter { !it.contentRaw.toLowerCase().matches(Regex("($prefix){1,2}archive")) }
		.joinToString(System.lineSeparator()) {

			val stringBuilder = StringBuilder("${it.author.fullName()}: ")

			if (it.embeds.isNotEmpty()) {
				it.embeds.forEach {

					stringBuilder.append(newline + embedNotation + newline)
					val fieldCount = it.fields.size

					it.fields.forEachIndexed { index, field ->
						stringBuilder
							.append(field.name).append(newline)
							.append(field.value).append(newline)

						if (index != fieldCount - 1)
							stringBuilder.append(newline)
					}

					stringBuilder.append(embedNotation).append(newline)
				}
			}
			else
				stringBuilder.append(it.fullContent())

			stringBuilder.toString()
		}
}
