package repcheck.shared.models.prompt

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InstructionBlockSpec extends AnyFlatSpec with Matchers {

  private val sampleBlock = InstructionBlock(
    name = "system-base",
    stage = PromptStage.System,
    weight = 1.0,
    version = "1.0.0",
    content = "You are a legislative analysis assistant.",
  )

  "InstructionBlock Circe codec" should "round-trip" in {
    sampleBlock.asJson.as[InstructionBlock] shouldBe Right(sampleBlock)
  }

  it should "round-trip with weight at boundaries" in {
    val w0 = sampleBlock.copy(weight = 0.0)
    w0.asJson.as[InstructionBlock] shouldBe Right(w0)
    val w1 = sampleBlock.copy(weight = 1.0)
    w1.asJson.as[InstructionBlock] shouldBe Right(w1)
    val wMid = sampleBlock.copy(weight = 0.5)
    wMid.asJson.as[InstructionBlock] shouldBe Right(wMid)
  }

  it should "fail when weight > 1.0" in {
    val json = """{"name":"test","stage":"system","weight":1.5,"version":"1.0.0","content":"test content"}"""
    decode[InstructionBlock](json).isLeft shouldBe true
  }

  it should "fail when weight < 0.0" in {
    val json = """{"name":"test","stage":"system","weight":-0.1,"version":"1.0.0","content":"test content"}"""
    decode[InstructionBlock](json).isLeft shouldBe true
  }

  it should "fail on missing required field" in {
    decode[InstructionBlock]("""{"name":"test","stage":"system","weight":0.5}""").isLeft shouldBe true
  }

}
