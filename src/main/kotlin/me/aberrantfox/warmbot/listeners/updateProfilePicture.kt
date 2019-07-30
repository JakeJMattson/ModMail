package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.descriptor
import me.aberrantfox.warmbot.services.LoggingService
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.user.update.UserUpdateAvatarEvent
import me.aberrantfox.warmbot.services.*
import java.awt.Color

class ProfilePictureListener (private val reportService: ReportService, private val loggingService: LoggingService) {
    @Subscribe
    fun getUserUpdateAvatarEvent(event: UserUpdateAvatarEvent) {
        val userProfilePicture = event.newAvatarUrl
        if (event.user.hasReportChannel()) {
            val updatedOpeningMessage = embed {
                addField("New Report Opened!", "${event.user.descriptor()} :: ${event.user.asMention}", false)
                setThumbnail(userProfilePicture)
                setColor(Color.green)
            }
            val reportMessages = event.user.userToReport() ?: return
            var channelMessageHistory = reportMessages.reportToChannel().iterableHistory.complete()
            channelMessageHistory.last().editMessage(updatedOpeningMessage).queue()
        }
    }
}