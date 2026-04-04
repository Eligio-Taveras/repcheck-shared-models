package repcheck.shared.models.prompt

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PromptStageSpec extends AnyFlatSpec with Matchers {

  "PromptStage.fromString" should "parse all values" in {
    PromptStage.fromString("System") shouldBe Right(PromptStage.System)
    PromptStage.fromString("Persona") shouldBe Right(PromptStage.Persona)
    PromptStage.fromString("Lens") shouldBe Right(PromptStage.Lens)
    PromptStage.fromString("Context") shouldBe Right(PromptStage.Context)
    PromptStage.fromString("Custom") shouldBe Right(PromptStage.Custom)
    PromptStage.fromString("Guardrails") shouldBe Right(PromptStage.Guardrails)
    PromptStage.fromString("Output") shouldBe Right(PromptStage.Output)
  }

  it should "have exactly 7 values" in {
    PromptStage.values.length shouldBe 7
  }

  it should "be case-insensitive" in {
    PromptStage.fromString("system") shouldBe Right(PromptStage.System)
    PromptStage.fromString("PERSONA") shouldBe Right(PromptStage.Persona)
    PromptStage.fromString("lens") shouldBe Right(PromptStage.Lens)
  }

  it should "return Left for unknown values" in {
    PromptStage.fromString("Unknown").isLeft shouldBe true
  }

  "PromptStage stageOrder" should "follow correct ordering" in {
    PromptStage.System.stageOrder shouldBe 0
    PromptStage.Persona.stageOrder shouldBe 1
    PromptStage.Lens.stageOrder shouldBe 2
    PromptStage.Context.stageOrder shouldBe 3
    PromptStage.Custom.stageOrder shouldBe 4
    PromptStage.Guardrails.stageOrder shouldBe 5
    PromptStage.Output.stageOrder shouldBe 6
  }

  "PromptStage Circe codec" should "round-trip all values" in {
    PromptStage.values.foreach(ps => ps.asJson.as[PromptStage] shouldBe Right(ps))
  }

  it should "serialize to lowercase" in {
    PromptStage.System.asJson.asString shouldBe Some("system")
    PromptStage.Output.asJson.asString shouldBe Some("output")
  }

}
