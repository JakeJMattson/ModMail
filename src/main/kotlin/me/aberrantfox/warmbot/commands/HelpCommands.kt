package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.kjdautils.api.dsl.embed
import java.awt.Color

@CommandSet
fun helpCommands() = commands {
    command("help") {
        execute {
            it.respond(embed {
                setColor(Color.GREEN)
                setAuthor("WarmBot Help Menu", "http://github.com/AberrantFox/WarmBot")
                setThumbnail(it.jda.selfUser.effectiveAvatarUrl)
                description("Below you can see information about how to use this bot.")

                field {
                    name = "Ping"
                    value = "Check the status of the bot"
                }

                field {
                    name = "Help"
                    value = "Display this menu"
                }

                field {
                    name = "Author"
                    value = "Display the bot author"
                }

                field {
                    name = "Close"
                    value = "Close the current report channel. The current report channel is whatever channel you execute" +
                            " this command in. Note: You can just delete the channel instead"
                }

                field {
                    name = "Archive"
                    value = "Archive the contents of the report as a text document in the archive channel."
                }

                field {
                    name = "Source"
                    value = "Display the source code via a github link"
                }
            })
        }
    }
}