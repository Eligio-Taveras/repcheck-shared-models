package repcheck.shared.models.prompt

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PromptProfileSpec extends AnyFlatSpec with Matchers {

  "PromptProfile Circe codec" should "round-trip" in {
    val profile = PromptProfile(
      name = "bill-analysis",
      chain = List(
        StageConfig(PromptStage.System, List("system-base"), 1.0),
        StageConfig(PromptStage.Persona, List("analyst-persona"), 0.8),
        StageConfig(PromptStage.Output, List("json-output"), 1.0)
      )
    )
    profile.asJson.as[PromptProfile] shouldBe Right(profile)
  }

  "StageConfig Circe codec" should "round-trip" in {
    val sc = StageConfig(PromptStage.Lens, List("fiscal-lens", "social-lens"), 0.7)
    sc.asJson.as[StageConfig] shouldBe Right(sc)
  }

}
