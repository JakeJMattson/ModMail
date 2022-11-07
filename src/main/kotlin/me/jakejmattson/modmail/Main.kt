package me.jakejmattson.modmail

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.extensions.plus
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.Locale
import me.jakejmattson.modmail.services.configFile
import java.awt.Color

@KordPreview
@PrivilegedIntent
fun main(args: Array<String>) {
    bot(args.firstOrNull()) {
        val configuration = data(configFile.path) { Configuration() }

        prefix {
            guild?.let { configuration[it]?.prefix } ?: " "
        }

        configure {
            commandReaction = null
            dualRegistry = false
            recommendCommands = false
            theme = Color(0x00BFFF)
            intents = Intent.Guilds + Intent.GuildMembers + Intent.GuildBans + Intent.DirectMessages + Intent.DirectMessageTyping + Intent.GuildMessageTyping
            defaultPermissions = Permissions(Permission.ManageMessages)
        }

        presence {
            playing(Locale.DISCORD_PRESENCE)
        }
    }
}