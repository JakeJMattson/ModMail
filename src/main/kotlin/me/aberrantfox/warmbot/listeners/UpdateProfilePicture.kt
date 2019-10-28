package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.api.dsl.toEmbedBuilder
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent

class UpdateProfilePicture {
    @Subscribe
    fun onUserUpdateAvatarEvent(event: UserUpdateAvatarEvent) {
        val userProfilePicture = event.newAvatarUrl

        if (!event.user.hasReportChannel()) return

        val report = event.user.userToReport() ?: return
        val reportChannel = report.reportToChannel() ?: return
        val firstMessage = reportChannel.iterableHistory.complete().last()

        if (firstMessage.embeds.isEmpty()) return

        val newReportEmbed = firstMessage.embeds[0].toEmbedBuilder().setThumbnail(userProfilePicture).build()
        firstMessage.editMessage(newReportEmbed).queue()
    }
}