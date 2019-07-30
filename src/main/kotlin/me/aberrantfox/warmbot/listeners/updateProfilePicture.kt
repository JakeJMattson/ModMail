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
        if (event.user.hasReportChannel()) {
            val reportMessages = event.user.userToReport() ?: return
            val channelMessageHistory = reportMessages.reportToChannel().iterableHistory.complete()
            val newEmbedForOpenReport = channelMessageHistory.last().embeds[0].toEmbedBuilder().setThumbnail(userProfilePicture).build()
            channelMessageHistory.last().editMessage(newEmbedForOpenReport).queue()
        }
    }

}