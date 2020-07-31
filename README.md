<a href="https://discord.gg/REZVVjA">
  <img alt="Discord Banner" src="https://discordapp.com/api/guilds/453208597082406912/widget.png?style=banner2"/>
</a>

# ModMail
ModMail is a Discord bot designed to provide a communication system between server staff and other members.

## Reports
Reports are private text channels that allow the entire staff team to communicate with a single member.

![Reports](https://i.imgur.com/7vgwc9E.png)

### Creating Reports
Reports will be opened automatically whenever a user messages the bot.
The `Open` command can be used to open a report manually, or `Detain` if you want to mute them as well.

### Using a Report
Once a report is opened, anyone with access to this private channel can talk with the user through the bot.
The user only needs to talk with the bot like a normal DM.

#### Closing a Report
##### From Discord
 * Delete the channel - ModMail will detect the event and close the report for you.

##### Using Commands
 * `Close` - This has the same effect as deleting the channel.
 * `Archive` - Transcribes the report to text, archives it, then closes the report.

## Setup
Refer to the [setup](setup.md) instructions.

## Commands
To see available commands, use `Help` or read the [commands](commands.md) documentation.