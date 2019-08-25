package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.extensions.toEmbedBuilder
import me.aberrantfox.warmbot.services.LoggingService
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.user.update.UserUpdateAvatarEvent
import me.aberrantfox.warmbot.services.*

class updateProfilePicture (private val reportService: ReportService, private val loggingService: LoggingService) {
    @Subscribe

    fun getUserUpdateAvatarEvent(event: UserUpdateAvatarEvent) {
        val userProfilePicture = event.newAvatarUrl

        if (!event.user.hasReportChannel()) { return }

        val reportMessages = event.user.userToReport() ?: return
        val channelMessages = reportMessages.reportToChannel().iterableHistory.complete().last()

        if (channelMessages.embeds.isEmpty()) { return }

        val newReportEmbed = channelMessages.embeds[0].toEmbedBuilder().setThumbnail(userProfilePicture).build()
        channelMessages.editMessage(newReportEmbed).queue()
    }
}