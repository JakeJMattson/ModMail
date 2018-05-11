package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.warmbot.listeners.ChannelDeletionListener
import me.aberrantfox.warmbot.listeners.ReportListener
import me.aberrantfox.warmbot.listeners.ResponseListener
import me.aberrantfox.warmbot.listeners.isStaffMember
import me.aberrantfox.warmbot.services.Configuration
import me.aberrantfox.warmbot.services.ReportService
import me.aberrantfox.warmbot.services.loadConfiguration
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Game

object ObjectRegister {
    private val register = HashMap<String, Any>()

    operator fun get(key: String) = register[key]
    operator fun set(key: String, value: Any) = register.put(key, value)
}

fun main(args: Array<String>) {
    val config = loadConfiguration()

    if(config == null) {
        println("Please fill in the configuration file (config/config.json)")
        return
    }

    start(config)
}

private fun start(config: Configuration) = startBot(config.token, config.prefix, "me.aberrantfox.warmbot") {
    val reportService = ReportService(jda, config)
    jda.addEventListener(
            ReportListener(reportService),
            ResponseListener(reportService, config.prefix),
            ChannelDeletionListener(reportService))

    ObjectRegister["reportService"] = reportService
    ObjectRegister["config"] = config

    addOverrides(jda, config)

    jda.presence.setPresence(Game.of(Game.GameType.DEFAULT, "DM to contact Staff"), true)
    registerCommandPrecondition(isStaffMember)
}

private fun addOverrides(jda: JDA, config: Configuration) {
    val staffRole = jda.getRolesByName(config.staffRoleName, true).first()
    val reportCategory = jda.getCategoryById(config.reportCategory)

    val isAvailableToStaff = reportCategory.permissionOverrides.any {
        it.isRoleOverride && it.role.name == staffRole.name && it.allowed.contains(Permission.MESSAGE_READ)
    }

    val isHiddenFromPublic = reportCategory.permissionOverrides
            .any { it.isRoleOverride && it.role.name == "@everyone" && it.denied.contains(Permission.MESSAGE_READ) }

    if(!isAvailableToStaff) {
        reportCategory.createPermissionOverride(staffRole).setAllow(Permission.MESSAGE_READ).queue()
    }

    if(!isHiddenFromPublic) {
        reportCategory.createPermissionOverride(staffRole.guild.publicRole).setDeny(Permission.MESSAGE_READ).queue()
    }
}