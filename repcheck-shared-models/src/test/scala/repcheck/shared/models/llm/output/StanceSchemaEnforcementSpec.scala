package repcheck.shared.models.llm.output

import io.circe.Json
import io.circe.parser.parse

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.llm.codec.StructuredCodec
import repcheck.shared.models.llm.{Effect, Impact, Scope}

/**
 * Proves the two-layer guarantee that the summarizer can only ever yield in-set stance values:
 *   1. CONSTRAIN — the JSON Schema the agentic `submit` tool sends the model enumerates EXACTLY the allowed
 *      effect/impact/scope values, so the model is told the closed set up front. 2. REJECT — the circe decoders
 *      (Effect/Impact/Scope.fromString) fail on any out-of-set value, so the structured-output enforcer in the agentic
 *      runner re-prompts instead of persisting a hallucinated value.
 * Together these answer "how do we ensure the LLM returns values from within the set" without trusting the model.
 */
class StanceSchemaEnforcementSpec extends AnyFlatSpec with Matchers {

  private val schema: Json = StructuredCodec[ConceptSummaryWithTopics].jsonSchema

  /** The `enum` constraint the schema publishes for one ConceptTopic field (effect/impact/scope). */
  private def schemaEnum(field: String): List[String] =
    schema.hcursor
      .downField("$defs")
      .downField("ConceptTopic")
      .downField("properties")
      .downField(field)
      .downField("enum")
      .as[List[String]]
      .getOrElse(Nil)

  "The summarizer submit-tool JSON schema" should "publish exactly the allowed effect values" in {
    schemaEnum("effect") should contain theSameElementsAs Effect.values.toList.map(_.apiValue)
  }

  it should "publish exactly the allowed impact values" in {
    schemaEnum("impact") should contain theSameElementsAs Impact.values.toList.map(_.apiValue)
  }

  it should "publish exactly the allowed scope values" in {
    schemaEnum("scope") should contain theSameElementsAs Scope.values.toList.map(_.apiValue)
  }

  "The ConceptTopic decoder" should "reject an out-of-set stance value so the enforcer re-prompts" in {
    val outOfSet =
      """{"phrase":"x","topic":"y","effect":"DELETES","entity":"z","impact":"POSITIVE","scope":"MAJOR"}"""
    parse(outOfSet).flatMap(_.as[ConceptTopic]).isLeft shouldBe true
  }

}
