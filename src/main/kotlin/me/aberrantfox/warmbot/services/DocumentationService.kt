package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.*
import java.io.File
import java.util.Timer
import kotlin.concurrent.schedule

private const val indentLevel: String = "##"

@Service
class DocumentationService(configuration: Configuration, container: CommandsContainer) {
    init {
        //Move the help command from the internal "utility" category, to the local "Utility" category
        container.commands.getValue("help").category = "Utility"

        if (configuration.generateDocsAtRuntime) {
            Timer().schedule(500) {
                createDocumentation(container, Output.FILE)
            }
        }
    }

    private fun createDocumentation(container: CommandsContainer, outputType: Output) {
        val sortOrder = arrayListOf("Owner", "Configuration", "Report", "ReportHelpers", "Info", "Macros", "Utility")

        val categories = container.commands.values.groupBy { it.category }
        val categoryDocs = generateDocsByCategory(categories)
        val sortedDocs = sortCategoryDocs(categoryDocs, sortOrder)

        outputDocs(sortedDocs, outputType)
    }

    private fun generateDocsByCategory(categories: Map<String, List<Command>>) =
        categories.map {
            data class CommandData(val name: String, val args: String, val description: String) {
                fun format(format: String) = String.format(format, name, args, description)
            }

            //Map the commands to a data class for easier manipulation
            val commandData = it.value.map {
                CommandData(
                    it.name,
                    it.expectedArgs.joinToString {
                        if (it.optional)
                            "(${it.type.name})"
                        else
                            it.type.name
                    }.takeIf { it.isNotEmpty() } ?: "<none>",
                    it.description.replace("|", "\\|")
                )
            } as ArrayList

            with(commandData) {
                operator fun String.times(x: Int) = this.repeat(x)

                //Determine the max width of the data in each column (including headers)
                val headers = CommandData("Commands", "Arguments", "Description")
                add(headers)
                val longestName = maxBy { it.name.length }!!.name.length
                val longestArgs = maxBy { it.args.length }!!.args.length
                val longestDescription = maxBy { it.description.length }!!.description.length
                val columnFormat = "| %-${longestName}s | %-${longestArgs}s | %-${longestDescription}s |"
                remove(headers)

                //Apply the column format to the command data
                val docs = StringBuilder()
                docs.appendln(headers.format(columnFormat))
                docs.appendln(String.format(columnFormat, "-" * longestName, "-" * longestArgs, "-" * longestDescription))

                sortedBy { it.name }.forEach {
                    docs.appendln(it.format(columnFormat))
                }

                CategoryDocs(it.key, docs.toString())
            }
        } as ArrayList<CategoryDocs>

    private fun sortCategoryDocs(categoryDocs: ArrayList<CategoryDocs>, sortOrder: ArrayList<String>): List<CategoryDocs> {
        val sortedMap = LinkedHashMap<String, CategoryDocs?>()
        val rogueCategories = arrayListOf<String>()

        //Populate the map keys with the desired sort order
        sortOrder.forEach {
            sortedMap[it] = null
        }

        //Populate the (sorted) map values with docs by name
        //If the sort order was not specified for a doc, it is appended to the end.
        categoryDocs.forEach {
            if (!sortedMap.containsKey(it.name))
                rogueCategories.add(it.name)

            sortedMap[it.name] = it
        }

        val deadCategories = sortedMap.filter { it.value == null }.map { it.key }

        with (rogueCategories) {
            if (isEmpty())
                return@with

            println("Found $size rogue categories not requested for sort. Appending to sorted docs: ${joinToString()}")
        }

        with (deadCategories) {
            if (isEmpty())
                return@with

            println("Found $size categories with no commands requested for sorting. Ignoring: ${joinToString()}")
        }

        //Remove dead keys (values with no data)
        return sortedMap.values.filterNotNull()
    }

    private fun outputDocs(docs: List<CategoryDocs>, outputType: Output) {
        val docsAsString = StringBuilder().apply {
            docs.forEach {
                appendln(it.toString())
            }
        }.toString()

        when (outputType) {
            Output.CONSOLE -> println(docsAsString)
            Output.FILE -> outputToFile(docsAsString)
        }
    }

    private fun outputToFile(docs: String) {
        val fileHeader =
            "# Commands\n\n" +
                "$indentLevel Key\n" +
                "| Symbol     | Meaning                    |\n" +
                "| ---------- | -------------------------- |\n" +
                "| (Argument) | This argument is optional. |\n"

        val fileText = "$fileHeader\n$docs"

        File("Commands.md").apply {
            writeText(fileText)
            println("Successfully generated documentation: $absolutePath")
        }
    }

    private data class CategoryDocs(val name: String, val docString: String) {
        override fun toString() = "$indentLevel $name\n$docString"
    }

    private enum class Output {
        CONSOLE, FILE
    }
}