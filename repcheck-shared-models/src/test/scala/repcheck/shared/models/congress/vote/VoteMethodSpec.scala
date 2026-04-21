package repcheck.shared.models.congress.vote

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class VoteMethodSpec extends AnyFlatSpec with Matchers {

  "VoteMethod.fromString" should "parse canonical apiValue values" in {
    val _ = VoteMethod.fromString("recorded vote") shouldBe Right(VoteMethod.RecordedVote)
    val _ = VoteMethod.fromString("voice vote") shouldBe Right(VoteMethod.VoiceVote)
    val _ = VoteMethod.fromString("unanimous consent") shouldBe Right(VoteMethod.UnanimousConsent)
    val _ = VoteMethod.fromString("roll") shouldBe Right(VoteMethod.Roll)
    val _ = VoteMethod.fromString("yea-and-nay") shouldBe Right(VoteMethod.YeaAndNay)
    val _ = VoteMethod.fromString("2/3 yea-and-nay") shouldBe Right(VoteMethod.TwoThirdsYeaAndNay)
    VoteMethod.fromString("quorum call") shouldBe Right(VoteMethod.QuorumCall)
  }

  it should "accept enum case names" in {
    val _ = VoteMethod.fromString("RecordedVote") shouldBe Right(VoteMethod.RecordedVote)
    val _ = VoteMethod.fromString("VoiceVote") shouldBe Right(VoteMethod.VoiceVote)
    val _ = VoteMethod.fromString("UnanimousConsent") shouldBe Right(VoteMethod.UnanimousConsent)
    val _ = VoteMethod.fromString("Roll") shouldBe Right(VoteMethod.Roll)
    val _ = VoteMethod.fromString("YeaAndNay") shouldBe Right(VoteMethod.YeaAndNay)
    val _ = VoteMethod.fromString("TwoThirdsYeaAndNay") shouldBe Right(VoteMethod.TwoThirdsYeaAndNay)
    VoteMethod.fromString("QuorumCall") shouldBe Right(VoteMethod.QuorumCall)
  }

  it should "be case-insensitive" in {
    val _ = VoteMethod.fromString("RECORDED VOTE") shouldBe Right(VoteMethod.RecordedVote)
    val _ = VoteMethod.fromString("Voice Vote") shouldBe Right(VoteMethod.VoiceVote)
    val _ = VoteMethod.fromString("ROLL") shouldBe Right(VoteMethod.Roll)
    // Congress.gov API emits title case: "Yea-and-Nay", "Quorum Call", etc. DB stores lowercase.
    val _ = VoteMethod.fromString("Yea-and-Nay") shouldBe Right(VoteMethod.YeaAndNay)
    val _ = VoteMethod.fromString("2/3 Yea-And-Nay") shouldBe Right(VoteMethod.TwoThirdsYeaAndNay)
    VoteMethod.fromString("Quorum Call") shouldBe Right(VoteMethod.QuorumCall)
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
    val _ = VoteMethod.UnanimousConsent.asJson.asString shouldBe Some("unanimous consent")
    val _ = VoteMethod.YeaAndNay.asJson.asString shouldBe Some("yea-and-nay")
    val _ = VoteMethod.TwoThirdsYeaAndNay.asJson.asString shouldBe Some("2/3 yea-and-nay")
    VoteMethod.QuorumCall.asJson.asString shouldBe Some("quorum call")
  }

  it should "fail to decode unknown JSON value" in {
    val json    = io.circe.Json.fromString("unknown method")
    val decoded = json.as[VoteMethod]
    decoded.isLeft shouldBe true
  }

}
