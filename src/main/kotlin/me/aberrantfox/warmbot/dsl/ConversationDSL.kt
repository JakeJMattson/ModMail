package me.aberrantfox.warmbot.dsl

data class Conversation(val name: String?, val description: String?,
                        val steps: List<Step>)

data class Step(val message: String, val responseType: ResponseType?) {
    enum class ResponseType { Guild, String, Integer, Channel, User }
}

fun conversation(block: ConversationBuilder.() -> Unit): Conversation = ConversationBuilder().apply(block).build()

class ConversationBuilder {
    var name = ""
    var description = ""
    private val steps = mutableListOf<Step>()

    fun steps(block: STEPS.() -> Unit) {
        steps.addAll(STEPS().apply(block))
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
    var responseType: Step.ResponseType = Step.ResponseType.String
    fun build(): Step = Step(message, responseType)
}