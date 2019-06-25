package me.aberrantfox.warmbot.utility

import me.aberrantfox.kjdautils.api.dsl.CommandsContainer

fun generateDocs(commandsContainer: CommandsContainer) =
    with(StringBuilder()) {
        operator fun String.times(x: Int) = this.repeat(x)
        data class CommandData(val name: String, val args: String, val description: String) {
            fun format(format: String) = String.format(format, name, args, description)
        }

        commandsContainer.commands.values.groupBy { it.category }.toList().forEach {
            appendln("### ${it.first}")

            val categoryCommands = it.second.map {
                CommandData(
                    it.name,
                    it.expectedArgs.joinToString {
                        if(it.optional)
                            "(${it.type.name})"
                        else
                            it.type.name
                    }.takeIf { it.isNotEmpty() } ?: "<none>",
                    it.description.replace("|", "\\|")
                )
            } as ArrayList

            with(categoryCommands) {
                val headers = CommandData("Commands", "Arguments", "Description")
                add(headers)

                val longestName = maxBy { it.name.length }!!.name.length
                val longestArgs = maxBy { it.args.length }!!.args.length
                val longestDescription = maxBy { it.description.length }!!.description.length
                val format = "| %-${longestName}s | %-${longestArgs}s | %-${longestDescription}s |"

                remove(headers)

                appendln(headers.format(format))
                appendln(String.format(format, "-" * longestName, "-" * longestArgs, "-" * longestDescription))

                sortedBy { it.name }.forEach {
                    appendln(it.format(format))
                }
            }

            appendln()
        }

        this.toString()
    }