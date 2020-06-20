package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.userToReport
import me.jakejmattson.kutils.api.dsl.embed.toEmbedBuilder
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent

class UpdateProfilePicture {
    @Subscribe
    fun onUserUpdateAvatarEvent(event: UserUpdateAvatarEvent) {
        val userProfilePicture = event.newAvatarUrl

        val report = event.user.userToReport() ?: return
        val reportChannel = report.reportToChannel() ?: return
        val firstMessage = reportChannel.iterableHistory.complete().last()

        if (firstMessage.embeds.isEmpty()) return

        val newReportEmbed = firstMessage.embeds[0].toEmbedBuilder().setThumbnail(userProfilePicture).build()
        firstMessage.editMessage(newReportEmbed).queue()
    }
}