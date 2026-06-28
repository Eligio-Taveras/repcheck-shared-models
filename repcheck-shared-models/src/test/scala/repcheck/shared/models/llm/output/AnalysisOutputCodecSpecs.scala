package repcheck.shared.models.llm.output

import io.circe.parser.parse

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.llm.codec.{StructuredCodec, StructuredCodecLaws}
import repcheck.shared.models.llm.{ImpactSeverity, PorkType, StanceType}

/** §10c #5b law for the Component-10 analysis LLM outputs — round-trip + schema-validate example and every sample. */
class StanceClassificationOutputCodecSpec
    extends StructuredCodecLaws[StanceClassificationOutput]("StanceClassificationOutput")

class PorkDetectionOutputCodecSpec extends StructuredCodecLaws[PorkDetectionOutput]("PorkDetectionOutput")

class ImpactAnalysisOutputCodecSpec extends StructuredCodecLaws[ImpactAnalysisOutput]("ImpactAnalysisOutput")

/**
 * The closed-set guarantee for the analysis outputs: the submit-tool JSON schema enumerates EXACTLY the allowed enum
 * values (constrain), and the circe decoders reject out-of-set values (reject — the agentic enforcer then re-prompts).
 */
class AnalysisOutputSchemaEnforcementSpec extends AnyFlatSpec with Matchers {

  private def schemaEnum(codec: StructuredCodec[?], defName: String, field: String): List[String] =
    codec.jsonSchema.hcursor
      .downField("$defs")
      .downField(defName)
      .downField("properties")
      .downField(field)
      .downField("enum")
      .as[List[String]]
      .getOrElse(Nil)

  "StanceClassificationOutput schema" should "enumerate exactly the StanceType values" in {
    schemaEnum(StructuredCodec[StanceClassificationOutput], "TopicStance", "stance") should
      contain theSameElementsAs StanceType.values.toList.map(_.apiValue)
  }

  "PorkDetectionOutput schema" should "enumerate exactly the PorkType and ImpactSeverity values" in {
    val c = StructuredCodec[PorkDetectionOutput]
    val _ =
      schemaEnum(c, "PorkFinding", "porkType") should contain theSameElementsAs PorkType.values.toList.map(_.apiValue)
    schemaEnum(c, "PorkFinding", "severity") should contain theSameElementsAs ImpactSeverity.values.toList
      .map(_.apiValue)
  }

  "ImpactAnalysisOutput schema" should "enumerate exactly the ImpactSeverity values" in {
    schemaEnum(StructuredCodec[ImpactAnalysisOutput], "ImpactItem", "severity") should
      contain theSameElementsAs ImpactSeverity.values.toList.map(_.apiValue)
  }

  "The analysis decoders" should "reject out-of-set enum values so the enforcer re-prompts" in {
    val _ = parse("""{"topic":"t","stance":"centrist","confidence":0.5,"reasoning":"r"}""")
      .flatMap(_.as[TopicStance])
      .isLeft shouldBe true
    val _ = parse("""{"porkType":"giveaway","description":"d","affectedSection":"s","severity":"high"}""")
      .flatMap(_.as[PorkFinding])
      .isLeft shouldBe true
    parse("""{"affectedGroup":"g","impactType":"t","description":"d","severity":"extreme"}""")
      .flatMap(_.as[ImpactItem])
      .isLeft shouldBe true
  }

}
