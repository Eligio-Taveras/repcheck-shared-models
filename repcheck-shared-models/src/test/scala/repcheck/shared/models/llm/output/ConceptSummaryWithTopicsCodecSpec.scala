package repcheck.shared.models.llm.output

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.llm.codec.StructuredCodecLaws
import repcheck.shared.models.llm.{Effect, Impact, Scope}

/** §10c #5b law: round-trip + schema-validate the canonical example and every generated sample. */
class ConceptSummaryWithTopicsCodecSpec
    extends StructuredCodecLaws[ConceptSummaryWithTopics]("ConceptSummaryWithTopics")

/** Directly exercises the nested ConceptTopic codec so its derived encoder/decoder are hit. */
class ConceptTopicCodecSpec extends AnyFlatSpec with Matchers {

  "ConceptTopic" should "round-trip via circe" in {
    val topic = ConceptTopic(
      "expanding solar tax credits",
      "solar energy incentives",
      Effect.Expands,
      "homeowners",
      Impact.Positive,
      Scope.Major,
    )
    topic.asJson.as[ConceptTopic] shouldBe Right(topic)
  }

}
