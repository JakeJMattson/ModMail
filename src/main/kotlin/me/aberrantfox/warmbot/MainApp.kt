package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.warmbot.listeners.*
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.*
import net.dv8tion.jda.core.entities.Game

fun main(args: Array<String>) {
    val config = loadConfiguration() ?: return
    start(config)
}

private fun start(config: Configuration) = startBot(config.token) {

    val loggingService = LoggingService(jda, config)
    val reportService = ReportService(jda, config, loggingService).apply {  loadReports() }

    registerInjectionObject(loggingService, reportService, conversationService, config)

	val warmbot = "me.aberrantfox.warmbot."
	configure {
		prefix = config.prefix
		commandPath = warmbot + "commands"
		listenerPath = warmbot + "listeners"
		conversationPath = warmbot + "conversations"
	}

	registerCommandPreconditions(produceIsStaffChannelPrecondition(config), produceIsStaffMemberPrecondition(config), produceIsGuildOwnerPrecondition())

    config.guildConfigurations.forEach {
        addOverrides(jda, it)
    }

    jda.presence.setPresence(Game.of(Game.GameType.DEFAULT, "DM to contact Staff"), true)
    loggingService.emitReadyMessage()
}

private fun addOverrides(jda: JDA, config: GuildConfiguration) {
    val staffRole = jda.getRolesByName(config.staffRoleName, true).first()
    val reportCategory = jda.getCategoryById(config.reportCategory)
    val archiveChannel = jda.getTextChannelById(config.archiveChannel)

    reportCategory.putPermissionOverride(staffRole).setAllow(Permission.MESSAGE_READ).queue()
    reportCategory.putPermissionOverride(reportCategory.guild.publicRole).setDeny(Permission.MESSAGE_READ).queue()

    archiveChannel.putPermissionOverride(staffRole).setAllow(Permission.MESSAGE_READ).queue()
    archiveChannel.putPermissionOverride(reportCategory.guild.publicRole).setDeny(Permission.MESSAGE_READ).queue()
}