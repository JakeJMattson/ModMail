<p align="center">
  <a href="https://kotlinlang.org/">
    <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-1.4.20-blue.svg?logo=Kotlin">
  </a>
  <a href="https://github.com/JakeJMattson/DiscordKt">
    <img alt="DiscordKt" src="https://img.shields.io/badge/DiscordKt-0.22.0-blue.svg?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAYAAACM/rhtAAAABmJLR0QA/wD/AP+gvaeTAAAEvElEQVRYw+2Yy2seVRiHn3Ob756kiWlrq7ZqDSJUsSqoK4srQXHjRnShf4ILl/4FrkVcKLgS1FW3FRR05b1IMVXbNG2SJvny5ct3nZkz5+IitpW2giYOFskLA7M4Z+Y57/m9P857YC92F+Lqy/T0M/F2Atvc/EIAyNs9g3uAuw3974lZMFc9jsJwLj+Dj+72ATxSO8KrB1/nHjOHd5q1rM3Hm+/zc/b9f1vFTV3npbuf49k7ThJ9gisM3ulrz5nBj3zSfY+u39hxFe8IUCA4efgRXjv2PA05iXeaiGHyiRa2L+l87SnsNmRaBE53T3F68CnuH2z7jgHvm5rljRPPMTdxz7VMVeemOPTyYSp3JgCMzlt+fWdA71zEO41zmqV0hY823mPBzpcHeKg1wYcvvEJCDe8V1GpMv3iM5qMzN42NEdpfjFn4YETakXinyQt4e/ktLtvz5Rj1s/cfpVWPmEpO88Emh988cUs4ACFg/8k6j707w+wT23NqVc+Tk0+X54ONisBUcnRiqT9/P157vPd/OT7GiFM5B16SJJWcpJJTM6o8m6lVBCaxSBmIaU5sVXGFxXuB1gYpr/48YnNLlmYgInISTDVDFoZ6mYB3tAw6sUjlQVyXbAyRwlqUUiAEaZoSQ7imcCEiSSXHK89krVoeoNEek1i8V3hxc01577F5QSTcIMhtDUrlSYwrD3CyLrcz6BUBzy3LPsuham7gu57BZjUpr0jqFYE2BdoUmIX5bS+5ack3fDKC+2ENnVhMJadqYnkZbA9ztCkIMiCWziJ7bewjTxFmD17nUX8CXB/gv7pAuDTCJBrvFZ1sUB5gp+9Z35LMTjm0CsjxKuarUxQH7iV/+ElivQFCIEYp6odzhF9WiU5jEo2QnvZajc1RiRo0JtIbw3CkmZkKTLX+yGb7POqzRYqDx1AuIi5dIRSCkGikDPQtLK9VGI8jifHlAQ5zi1YFISjWupLeSLN/2lE1AekDanWe4BVBK7xQZCGy1jV0uooQLcoERn5YogZHGd3UMtXQaKmwXnLpimKiEZmddmgp8TIQRaDT1bTbGlsEjLE4qehmlp4dlQfoY6CfFgxTz76mpllVSKkYZprhkqHViOAl3X5CnguEdhgiQ+9pDwNpIfEyLw/w4sYYj0NIxcYwMBgrZiY0iY5EL+mPFdFHoijQJuKjY3Mo2RpFolBECYuD1fJ8sJ85Pp/f4EpvjFKBAsdyz9IeWKJyaO1QxiGVo59nLG9ljIscpR1bdsB36xdJfVZmkXhSF/lmsc++dsrxuxrsq1VIi8jlTqBZUcQg6I8FtpAorcl9wfzGgKWtETEKBkVa4hZ3Uu6cSjg6U6U7Lvjy1x5376vw0KEGiTQMck8MEoREKMFCZ8C59SHOgZSSy70Ri71u+U3T/omEx4+2mKprQJBIwYOHGhyZrkFULG9Zzi6NGeWeGCVjG/hppcul7t+3mF01TVcnHp2t8tiRFonePuNJISAIfIQYBD4IfmuP+Gmlh/NhR13djvviCCy0M1a6OcfvavLAgfr2IUtEBLDSt3y32GeY766B33XjnrvItxcH/LaeMXewipaCC+2M1Z69va4+tsYFX18o9m639gD/d4B7sdv4Hc8YdmfiizvVAAAAAElFTkSuQmCC">
  </a><br>
  <a href="https://GitHub.com/JakeJMattson/ModMail/releases/">
    <img alt="Release" src="https://img.shields.io/github/release/JakeJMattson/ModMail.svg?label=Release&logo=Github">
  </a>
  <a href="https://hub.docker.com/repository/docker/jakejmattson/modmail/tags?page=1">
    <img alt="Docker" src="https://img.shields.io/docker/cloud/build/jakejmattson/modmail.svg?label=Docker&logo=docker">
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