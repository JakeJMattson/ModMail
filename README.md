<p align="center">
   <a href="https://kotlinlang.org/">
   <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-1.7.10-%23A97BFF.svg?logo=Kotlin">
   </a>
   <a href="https://github.com/discordkt/discordkt/releases/">
   <img alt="Version" src="https://img.shields.io/badge/Version-0.23.3-%23E15ABA?label=Version&logo=GitHub">
   </a>
   <br>
   <a href="https://discordapp.com/users/254786431656919051/">
   <img alt="Discord JakeyWakey#1569" src="https://img.shields.io/badge/Personal-JakeyWakey%231569-%2300BFFF.svg?logo=discord">
   </a>
</p>

# ModMail

ModMail is a Discord bot designed to provide a communication system between server staff and other members.

## Reports

Reports are private text channels that allow the entire staff team to communicate with a single member.

![Reports](https://i.imgur.com/7vgwc9E.png)

### Creating Reports

Reports will be opened automatically whenever a user messages the bot.
The `/Open` command can be used to open a report manually, or `/Detain` if you want to mute them as well.

### Using a Report

Once a report is opened, anyone with access to this private channel can talk with the user through the bot.
The user only needs to talk with the bot like a normal DM.

#### Closing a Report

##### From Discord

* Delete the channel - ModMail will detect the event and close the report for you.

##### Using Commands

* `/Close` - This has the same effect as deleting the channel.
* `/Archive` - Transcribes the report to text, archives it, then closes the report.

## Setup

Refer to the [setup](setup.md) instructions.

## Commands

To see available commands, use `Help` or read the [commands](commands.md) documentation.