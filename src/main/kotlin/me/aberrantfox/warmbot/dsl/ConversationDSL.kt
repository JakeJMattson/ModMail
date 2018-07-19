package me.aberrantfox.warmbot.dsl

import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.MessageEmbed

class Conversation(val name: String, val description: String,
                   val steps: List<Step>, var onComplete: (ConversationStateContainer) -> Unit = {})

data class Step(val prompt: Any, val expectedResponseType: ResponseType?) {
    enum class ResponseType { Guild, String, Integer, Channel, User, Category }
}

data class ConversationStateContainer(val userId: String, val guildId: String, var responses: MutableList<Any>,
                                      val conversation: Conversation, var currentStep: Int,
                                      val jda: JDA, var config: Configuration) {

    fun respond(message: String) = jda.getUserById(userId).sendPrivateMessage(message)
    fun respond(embed: MessageEmbed) = jda.getUserById(userId).sendPrivateMessage(embed)
}

fun conversation(block: ConversationBuilder.() -> Unit): Conversation = ConversationBuilder().apply(block).build()

class ConversationBuilder {
    var name = ""
    var description = ""
    private val steps = mutableListOf<Step>()
    private var onComplete: (ConversationStateContainer) -> Unit = {}

    fun steps(block: STEPS.() -> Unit) {
        steps.addAll(STEPS().apply(block))
    }

    fun onComplete(onComplete: (ConversationStateContainer) -> Unit) {
        this.onComplete = onComplete
    }

    fun build(): Conversation = Conversation(name, description, steps, onComplete)
}

class STEPS : ArrayList<Step>() {
    fun step(block: StepBuilder.() -> Unit) {
        add(StepBuilder().apply(block).build())
    }
}

class StepBuilder {
    var prompt: Any = ""
    var expectedResponseType: Step.ResponseType = Step.ResponseType.String
    fun build(): Step = Step(prompt, expectedResponseType)
}

@Target(AnnotationTarget.FIELD)
annotation class Convo