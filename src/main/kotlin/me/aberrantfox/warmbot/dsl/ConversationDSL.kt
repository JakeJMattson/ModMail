package me.aberrantfox.warmbot.dsl

import me.aberrantfox.warmbot.services.ConversationStateContainer

class Conversation(val name: String, val description: String,
                        val steps: List<Step>, var onComplete: (ConversationStateContainer) -> Unit = {}) {
}

data class Step(val message: String, val responseType: ResponseType?) {
    enum class ResponseType { Guild, String, Integer, Channel, User }
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

    fun build(): Conversation = Conversation(name, description, steps)
}

class STEPS : ArrayList<Step>() {
    fun step(block: StepBuilder.() -> Unit) {
        add(StepBuilder().apply(block).build())
    }
}

class StepBuilder {
    var message: String = ""
    var expectedResponseType: Step.ResponseType = Step.ResponseType.String
    fun build(): Step = Step(message, expectedResponseType)
}