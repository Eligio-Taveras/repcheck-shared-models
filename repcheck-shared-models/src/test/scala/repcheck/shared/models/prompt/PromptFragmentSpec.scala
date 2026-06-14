package repcheck.shared.models.prompt

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PromptFragmentSpec extends AnyFlatSpec with Matchers {

  private val samplePromptFragment = PromptFragment(
    name = "system-base",
    stage = PromptStage.System,
    weight = 1.0,
    version = "1.0.0",
    content = "You are a legislative analysis assistant.",
  )

  "PromptFragment Circe codec" should "round-trip" in {
    samplePromptFragment.asJson.as[PromptFragment] shouldBe Right(samplePromptFragment)
  }

  it should "round-trip with weight at boundaries" in {
    val w0   = samplePromptFragment.copy(weight = 0.0)
    val _    = w0.asJson.as[PromptFragment] shouldBe Right(w0)
    val w1   = samplePromptFragment.copy(weight = 1.0)
    val _    = w1.asJson.as[PromptFragment] shouldBe Right(w1)
    val wMid = samplePromptFragment.copy(weight = 0.5)
    wMid.asJson.as[PromptFragment] shouldBe Right(wMid)
  }

  it should "fail when weight > 1.0" in {
    val json = """{"name":"test","stage":"system","weight":1.5,"version":"1.0.0","content":"test content"}"""
    decode[PromptFragment](json).isLeft shouldBe true
  }

  it should "fail when weight < 0.0" in {
    val json = """{"name":"test","stage":"system","weight":-0.1,"version":"1.0.0","content":"test content"}"""
    decode[PromptFragment](json).isLeft shouldBe true
  }

  it should "fail on missing required field" in {
    decode[PromptFragment]("""{"name":"test","stage":"system","weight":0.5}""").isLeft shouldBe true
  }

  it should "reject an out-of-range weight built from a valid PromptFragment" in {
    val outOfRange = samplePromptFragment.copy(weight = 2.0)
    outOfRange.asJson.as[PromptFragment] match {
      // Reading the failure's history/message forces the by-name args of DecodingFailure
      case Left(df) =>
        val _ = df.history.length should be >= 0
        df.getMessage should include("weight must be between")
      case Right(r) => fail(s"expected Left for weight 2.0, got $r")
    }
  }

  it should "reject a negative weight built from a valid PromptFragment" in {
    val negative = samplePromptFragment.copy(weight = -0.5)
    negative.asJson.as[PromptFragment] match {
      case Left(df) =>
        val _ = df.history.length should be >= 0
        df.getMessage should include("weight must be between")
      case Right(r) => fail(s"expected Left for weight -0.5, got $r")
    }
  }

}
