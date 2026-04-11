package repcheck.shared.models.congress.vote

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class VoteMethodSpec extends AnyFlatSpec with Matchers {

  "VoteMethod.fromString" should "parse canonical apiValue values" in {
    val _ = VoteMethod.fromString("recorded vote") shouldBe Right(VoteMethod.RecordedVote)
    val _ = VoteMethod.fromString("voice vote") shouldBe Right(VoteMethod.VoiceVote)
    val _ = VoteMethod.fromString("unanimous consent") shouldBe Right(VoteMethod.UnanimousConsent)
    VoteMethod.fromString("roll") shouldBe Right(VoteMethod.Roll)
  }

  it should "accept enum case names" in {
    val _ = VoteMethod.fromString("RecordedVote") shouldBe Right(VoteMethod.RecordedVote)
    val _ = VoteMethod.fromString("VoiceVote") shouldBe Right(VoteMethod.VoiceVote)
    val _ = VoteMethod.fromString("UnanimousConsent") shouldBe Right(VoteMethod.UnanimousConsent)
    VoteMethod.fromString("Roll") shouldBe Right(VoteMethod.Roll)
  }

  it should "be case-insensitive" in {
    val _ = VoteMethod.fromString("RECORDED VOTE") shouldBe Right(VoteMethod.RecordedVote)
    val _ = VoteMethod.fromString("Voice Vote") shouldBe Right(VoteMethod.VoiceVote)
    VoteMethod.fromString("ROLL") shouldBe Right(VoteMethod.Roll)
  }

  it should "return Left for unknown values" in {
    val result = VoteMethod.fromString("unknown method")
    val _      = result.isLeft shouldBe true
    result.left.map(_.getMessage) should matchPattern {
      case Left(msg: String) if msg.contains("unknown method") =>
    }
  }

  "VoteMethod Circe codec" should "round-trip values" in {
    VoteMethod.values.foreach { vm =>
      val json    = vm.asJson
      val decoded = json.as[VoteMethod]
      decoded shouldBe Right(vm)
    }
  }

  it should "encode to apiValue" in {
    val _ = VoteMethod.RecordedVote.asJson.asString shouldBe Some("recorded vote")
    VoteMethod.UnanimousConsent.asJson.asString shouldBe Some("unanimous consent")
  }

  it should "fail to decode unknown JSON value" in {
    val json    = io.circe.Json.fromString("unknown method")
    val decoded = json.as[VoteMethod]
    decoded.isLeft shouldBe true
  }

}
