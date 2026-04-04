package repcheck.shared.models.prompt

import io.circe.parser.{decode, decodeAccumulating}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StageConfigSpec extends AnyFlatSpec with Matchers {

  "StageConfig Circe codec" should "round-trip with all fields" in {
    val config = StageConfig(
      stage = PromptStage.System,
      blockNames = List("intro", "context", "task"),
      weight = 1.0,
    )
    config.asJson.as[StageConfig] shouldBe Right(config)
  }

  it should "round-trip with empty blockNames" in {
    val config = StageConfig(
      stage = PromptStage.Persona,
      blockNames = List.empty,
      weight = 0.5,
    )
    config.asJson.as[StageConfig] shouldBe Right(config)
  }

  it should "round-trip with every PromptStage value" in {
    val stages = List(
      PromptStage.System,
      PromptStage.Persona,
      PromptStage.Lens,
      PromptStage.Context,
      PromptStage.Custom,
      PromptStage.Guardrails,
      PromptStage.Output,
    )
    stages.foreach { stage =>
      val config = StageConfig(stage = stage, blockNames = List("block-a"), weight = 0.8)
      config.asJson.as[StageConfig] shouldBe Right(config)
    }
  }

  it should "round-trip with multiple blockNames" in {
    val config = StageConfig(
      stage = PromptStage.Context,
      blockNames = List("bill-text", "vote-history", "member-profile"),
      weight = 2.5,
    )
    config.asJson.as[StageConfig] shouldBe Right(config)
  }

  it should "round-trip with weight of 0.0" in {
    val config = StageConfig(stage = PromptStage.Guardrails, blockNames = List("safety"), weight = 0.0)
    config.asJson.as[StageConfig] shouldBe Right(config)
  }

  it should "fail to decode with missing required fields" in {
    decode[StageConfig]("""{"stage":"system"}""").isLeft shouldBe true
  }

  it should "encode stage using apiValue" in {
    val config = StageConfig(stage = PromptStage.Output, blockNames = List("result"), weight = 1.0)
    val json   = config.asJson
    json.hcursor.downField("stage").as[String] shouldBe Right("output")
    json.hcursor.downField("weight").as[Double] shouldBe Right(1.0)
    json.hcursor.downField("blockNames").as[List[String]] shouldBe Right(List("result"))
  }

  it should "decodeAccumulating valid JSON" in {
    val json = """{"stage":"system","blockNames":["intro"],"weight":1.0}"""
    decodeAccumulating[StageConfig](json).isValid shouldBe true
  }

  it should "decodeAccumulating invalid field types" in {
    val json = """{"stage":123,"blockNames":"not-a-list","weight":"bad"}"""
    decodeAccumulating[StageConfig](json).isInvalid shouldBe true
  }

  it should "decodeAccumulating missing required fields" in {
    decodeAccumulating[StageConfig]("""{}""").isInvalid shouldBe true
  }

}
