package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.stdlib.sanitiseMentions
import me.aberrantfox.warmbot.extensions.fullContent
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.events.message.guild.*
import net.dv8tion.jda.core.events.message.priv.*
import net.dv8tion.jda.core.events.user.UserTypingEvent
import java.awt.Color

class EditListener(private val reportService: ReportService) {
	@Subscribe
	fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
		if (!reportService.isReportChannel(event.channel.id)) return

		if (event.author.id == reportService.jda.selfUser.id) return

		val report = reportService.getReportByChannel(event.channel.id)
		val privateChannel = reportService.jda.privateChannels.first { it.user.id == report.userId }
		val targetMessage = report.messages[event.messageId]

		privateChannel.editMessageById(targetMessage, event.message).queue()
	}

	@Subscribe
	fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
		if (!reportService.isReportChannel(event.channel.id)) return

		val report = reportService.getReportByChannel(event.channel.id)
		val targetMessage = report.messages[event.messageId] ?: return
		val privateChannel = reportService.jda.privateChannels.first { it.user.id == report.userId }

		privateChannel.deleteMessageById(targetMessage).queue()
		report.messages.remove(event.messageId)

		reportService.writeReportToFile(report)
	}

	@Subscribe
	fun onUserTypingEvent(event: UserTypingEvent) {
		if (!reportService.hasReportChannel(event.user.id)) return

		event.privateChannel ?: return

		val report = reportService.getReportByUserId(event.user.id)
		val channel = reportService.jda.getTextChannelById(report.channelId)

		channel.sendTyping().queue()
	}

	@Subscribe
	fun onPrivateMessageUpdate(event: PrivateMessageUpdateEvent) {
		if (!reportService.hasReportChannel(event.author.id)) return

		fun trimMessage(message: Message) = message.fullContent().trimEnd().sanitiseMentions()
		fun createFields(title: String, message: String) = message.chunked(1024).mapIndexed { index, chunk ->
			MessageEmbed.Field(if (index == 0) title else "(cont)", chunk, false)
		}

		val report = reportService.getReportByUserId(event.author.id)
		val targetMessage = report.messages[event.messageId] ?: return
		val channel = reportService.jda.getTextChannelById(report.channelId)
		val guildMessage = channel.getMessageById(targetMessage).complete()

		val embed = embed {

			addField("Edit Detected!", "The user has performed a message edit.", false)

			createFields("Old Content", trimMessage(guildMessage)).forEach {
				addField(it)
			}
			createFields("New Content", trimMessage(event.message)).forEach {
				addField(it)
			}
			setColor(Color.YELLOW)
		}

		channel.sendMessage(embed).queue()
		channel.editMessageById(targetMessage, event.message).queue()
	}

	@Subscribe
	fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
		if (event.author.id == reportService.jda.selfUser.id) {
			if (event.message.embeds.isNotEmpty()) return

			val user = reportService.jda.getPrivateChannelById(event.message.channel.id).user.id

			if (!reportService.hasReportChannel(user)) return

			val report = reportService.getReportByUserId(user)

			if (report.queuedMessageId != null) {
				report.messages[report.queuedMessageId!!] = event.messageId
				report.queuedMessageId = null

				reportService.writeReportToFile(report)
			}
		}
	}

	@Subscribe
	fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
		if (event.author.id == reportService.jda.selfUser.id) {
			if (event.message.embeds.isNotEmpty()) return

			if (!reportService.isReportChannel(event.channel.id)) return

			val report = reportService.getReportByChannel(event.channel.id)

			if (report.queuedMessageId != null) {
				report.messages[report.queuedMessageId!!] = event.messageId
				report.queuedMessageId = null

				reportService.writeReportToFile(report)
			}
		}
	}
}