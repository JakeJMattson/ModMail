# Version 3.0.2

### Misc Changes
Upgraded from KUtils 0.9.17 to KUtils 0.13.0 and implemented the following upgrades
```
* Type inference - Command arguments are now type inferred; used destructuring where possible.
* Improved Help  - Removed ListCommands in favor of the improved KUtils help system.
* Mention embed  - Added a response on ping and replaced the following: Source, Author, BotInfo, Version
* Permissions    - Added an improved permissions system and replaced the duplicate preconditions.
* Conversations  - Conversations were completely re-written to match the new standard.
```

### Bug Fixes
```
* ResetTags - Resetting tags will now work with users who have spaces in their names.
```

# Version 3.0.1

### New Commands
```
* IsReport    - Check whether or not a channel is a valid report channel.
* PeekHistory - Read the target user's DM history with the bot.
```

### Misc Changes
```
* AuditLogPollingService - This feature was removed. Manual channel deletions now refer you to the audit log.
* Logging service        - The logging service will now log various errors as well as previous information.
* Better archives        - Default notes including the user's ID are now added to all archived reports.
* Detain command         - The detain command can now take an initial message and can no longer detain staff.
* Macro persistence      - Macros are now saved during runtime and loaded in on startup. 
* Docker persistence     - Persistent data is now mapped to disk and recoverable outside a docker container.
* Documentation service  - Add in the DocumentationService for generating documentation at runtime.
```

### Bug Fixes
```
* Improper unmute  - Closing the report of a user who is not detained will no longer unmute them.
* Migration embeds - Migration embeds are no longer triggered by user migration in a shared server.
* Fail reaction    - Fail reactions will now be added into reports, even with no shared server.
* Archive logging  - Archived reports will no longer trigger the manual deletion logging message.
* Embed thumbnails - Fixed thumbnails in embeds where the user has a default avatar (no pfp set).
```

# Version 3.0.0

### New Features
```
* Auto setup     - Automatically create required channels. If any of these channels exist, smart bind by name.
* Category sync  - The move command will now sync permissions with the new category. (Option to prevent sync)
* Log commands   - Remove more in-place embeds and with plain text logs sent to the logging channel.
* JUnit tests    - Added the backbone for adding tests and implemented for several command sets.
* Detainment     - Added commands and services that allow staff to mute users and begin a dialog.
* De-activation  - When a user leaves a server, their report is now deactivated. No messages are propagated.
* Fail reaction  - When a message is not delivered (currently due to deactivation), the bot will react with a red X.
* Rejoin resume  - When a user rejoins a server with an active report, it will be reactivated and the report will be notified.
* Channel config - Staff channels are now configurable insterad of automatic. The automatic process was too fragile.
* Macros         - Add pre-configured messages that can be sent through reports. This prevents repeated re-typing.
```

### New Commands
```
* SetPresence        - Set the Discord presence of the bot.
* Detain             - Mute a user and open a report with them.
* Release            - Remove a user from the detainment list and unmute them.
* AddStaffChannel    - Whitelist a channel. The bot will now respond to commands in this channel.
* RemoveStaffChannel - Unwhitelist a channel. The bot will no longer respond to commands in this channel.
* ListStaffChannels  - List the whitelisted channels (the channels where the bot will listen to commands).
* SendMacro          - Send a macro's message through a report channel.
* AddMacro           - Add a macro with a name and its response.
* RemoveMacro        - Removes a macro with the given name.
* RenameMacro        - Change a macro's name.
* EditMacro          - Change a macro's response.
* ListMacros         - List all of the currently available macros.
* ListCommands       - List all available commands.
```

### Fixes
```
* Ignore (do not log) audit log events from self.
* Remove all instances of hard-coded prefixes - config only.
* Fix issue where the invocation of the archive command was being archived.
```

# Version 2.0.1

### New Commands
```
* SetLoggingChannel - Set the target logging channel during runtime.
* Info              - Access report data such as user ID's.
* Move              - Move a target report to a different category.
* Tag               - Prepend a tag to the name of this report channel.
* ResetTags         - Reset a report channel to its original name.
```

### Misc Changes
```
* Added Docker deployment script and instructions for Windows.
* Report open embeds and edit embeds now contain a user's avatar.
* Message edits are now logged in the logging channel instead of in-place.
* Added an argument to the archive command to allow leaving notes next to files.
```

### Bug Fixes
```
* Logging service - Command events used in reports where users were no longer in the server would not log due to JDA.
* Logging service - Reports closed by channel deletion would not log due to human oversight.
* Archiving       - Messages containing links were transcribed as empty embeds due to Discord's preview feature.
* Editing         - Edits were intentionally not being sanitized due to lack of ping risk. They are now sanitized.
```

# Version 2.0.0

### New Features
```
* Multi-guild       - A single instance of this bot can now be used across multiple guilds.
* Event propagation - User and staff events can now be forwarded through the bot. 
                      User typing events and message edits will be forwarded to the private channel. 
                      Staff edits and deletes will be forwarded to the user.
* Report recovery   - Reports can now be saved to disk and reloaded if the bot goes offline instead of losing reports.
* Leave listener    - Create an embed in a report channel if the user that owns this report leaves or is banned.
* Logging           - Log events such as channel creation / deletion (and other events) into a logging channel.
* Whitelisting      - Ignore commands in non-staff channels. Leave non-whitelisted servers or initialize setup.
```

### New Commands
```
* Whitelist     - Add a guild to the whitelist.
* UnWhitelist   - Remove a guild from the whitelist.
* ShowWhitelist - Display all guilds in the whitelist.
```
```
* SetStaffRole      - Set the role required to use this bot.
* SetReportCategory - Set the category where new reports will be opened.
* SetArchiveChannel - Set the channel where reports will be archived.
```
```
* CloseAll - Close all of the currently open reports on the server.
* Open     - Open a report with the target user.
* Note     - Send an embed in the invoked channel as a note.
* Version  - Display the version of this instance of the bot.
* BotInfo  - Display various bot information (author; contributors; source; version).
* Uptime   - Display time the bot has been online since startup.
```

### Misc Changes
```
* The help system was upgraded through KUtils to be interactive.
* New reports start with embeds instead of plain text.
* The archive command can now handle embeds.
* Docker now available for deployments
```

# Version 1.5.1 and earlier

### Features
```
* Basic report functionality
```

### Commands
```
* Author  - Display the author of the bot.
* Source  - Display the GitLab repository link.
* Ping    - Display the network status of the bot.
* Help    - Display a basic static help menu.
* Close   - Close the report channel this command was invoked in.
* Archive - Archive the report channel this command was invoked in. This transcribes the report to a text document.
```
