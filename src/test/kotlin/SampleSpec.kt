import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertEquals

object SampleSpec : Spek({
    Feature("Test") {
        Scenario("Test") {
            Then("Test works") {
                assertEquals(1, 1)
            }
        }
    }
})