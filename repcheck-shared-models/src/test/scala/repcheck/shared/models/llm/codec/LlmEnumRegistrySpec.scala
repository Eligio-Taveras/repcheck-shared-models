package repcheck.shared.models.llm.codec

import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.llm.{Effect, Impact, ImpactSeverity, PorkType, Scope, StanceType}
import sttp.apispec.circe._
import sttp.tapir.Schema
import sttp.tapir.docs.apispec.schema.TapirSchemaToJsonSchema

/**
 * Backstop for the closed-set guarantee: every LLM-facing enum must — via [[LlmEnumCompanion]] — publish its closed set
 * as a JSON-schema `enum` (constrain) AND reject out-of-set values (reject). A new LLM enum should be registered here
 * and derive `LlmEnumCompanion`; this spec fails if a registered enum loses either half.
 */
class LlmEnumRegistrySpec extends AnyFlatSpec with Matchers {

  private def schemaEnum[E](using sch: Schema[E]): List[String] =
    TapirSchemaToJsonSchema(sch, markOptionsAsNullable = true).asJson.hcursor
      .downField("enum")
      .as[List[String]]
      .getOrElse(Nil)

  private def assertClosedSet[E <: LlmEnum](
    values: Array[E]
  )(using Schema[E], Encoder[E], Decoder[E]): org.scalatest.Assertion = {
    val _ = schemaEnum[E] should contain theSameElementsAs values.toList.map(_.apiValue) // constrain
    val _ = values.foreach(e => e.asJson.as[E] shouldBe Right(e))                        // round-trip
    Json.fromString("__definitely_not_a_member__").as[E].isLeft shouldBe true // reject
  }

  "Every LLM-facing enum" should "publish its closed set and reject out-of-set values" in {
    val _ = assertClosedSet(Effect.values)
    val _ = assertClosedSet(Impact.values)
    val _ = assertClosedSet(Scope.values)
    val _ = assertClosedSet(StanceType.values)
    val _ = assertClosedSet(PorkType.values)
    assertClosedSet(ImpactSeverity.values)
  }

}
