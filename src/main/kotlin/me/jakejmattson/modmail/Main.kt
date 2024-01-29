package me.jakejmattson.modmail

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.Locale
import me.jakejmattson.modmail.services.configFile
import java.awt.Color

@PrivilegedIntent
fun main(args: Array<String>) {
    bot(args.firstOrNull()) {
        data(configFile.path) { Configuration() }

        configure {
            theme = Color(0x00BFFF)
            intents = Intent.Guilds + Intent.GuildMembers + Intent.GuildModeration +
                    Intent.MessageContent + Intent.GuildMessages + Intent.DirectMessages +
                    Intent.DirectMessageTyping + Intent.GuildMessageTyping

            defaultPermissions = Permissions(Permission.ManageMessages)
        }

        presence {
            playing(Locale.DISCORD_PRESENCE)
        }
    }
}