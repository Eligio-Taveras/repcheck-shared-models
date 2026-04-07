package repcheck.shared.models.prompt

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PromptStageSpec extends AnyFlatSpec with Matchers {

  "PromptStage.fromString" should "parse all values" in {
    val _ = PromptStage.fromString("System") shouldBe Right(PromptStage.System)
    val _ = PromptStage.fromString("Persona") shouldBe Right(PromptStage.Persona)
    val _ = PromptStage.fromString("Lens") shouldBe Right(PromptStage.Lens)
    val _ = PromptStage.fromString("Context") shouldBe Right(PromptStage.Context)
    val _ = PromptStage.fromString("Custom") shouldBe Right(PromptStage.Custom)
    val _ = PromptStage.fromString("Guardrails") shouldBe Right(PromptStage.Guardrails)
    PromptStage.fromString("Output") shouldBe Right(PromptStage.Output)
  }

  it should "have exactly 7 values" in {
    PromptStage.values.length shouldBe 7
  }

  it should "be case-insensitive" in {
    val _ = PromptStage.fromString("system") shouldBe Right(PromptStage.System)
    val _ = PromptStage.fromString("PERSONA") shouldBe Right(PromptStage.Persona)
    PromptStage.fromString("lens") shouldBe Right(PromptStage.Lens)
  }

  it should "return Left for unknown values" in {
    PromptStage.fromString("Unknown").isLeft shouldBe true
  }

  "PromptStage stageOrder" should "follow correct ordering" in {
    val _ = PromptStage.System.stageOrder shouldBe 0
    val _ = PromptStage.Persona.stageOrder shouldBe 1
    val _ = PromptStage.Lens.stageOrder shouldBe 2
    val _ = PromptStage.Context.stageOrder shouldBe 3
    val _ = PromptStage.Custom.stageOrder shouldBe 4
    val _ = PromptStage.Guardrails.stageOrder shouldBe 5
    PromptStage.Output.stageOrder shouldBe 6
  }

  "PromptStage Circe codec" should "round-trip all values" in {
    PromptStage.values.foreach(ps => ps.asJson.as[PromptStage] shouldBe Right(ps))
  }

  it should "serialize to lowercase" in {
    val _ = PromptStage.System.asJson.asString shouldBe Some("system")
    PromptStage.Output.asJson.asString shouldBe Some("output")
  }

}
