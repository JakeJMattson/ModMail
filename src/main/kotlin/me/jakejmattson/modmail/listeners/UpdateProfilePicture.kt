package me.jakejmattson.modmail.listeners

import com.google.common.eventbus.Subscribe
import me.jakejmattson.modmail.services.findReport
import me.jakejmattson.kutils.api.dsl.embed.toEmbedBuilder
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent

class UpdateProfilePicture {
    @Subscribe
    fun onUserUpdateAvatarEvent(event: UserUpdateAvatarEvent) {
        val userProfilePicture = event.newAvatarUrl
        val report = event.user.findReport()?.toLiveReport(event.jda) ?: return
        val firstMessage = report.channel.iterableHistory.complete().last().takeUnless { it.embeds.isEmpty() } ?: return
        val newReportEmbed = firstMessage.embeds[0].toEmbedBuilder().setThumbnail(userProfilePicture).build()
        firstMessage.editMessage(newReportEmbed).queue()
    }
}