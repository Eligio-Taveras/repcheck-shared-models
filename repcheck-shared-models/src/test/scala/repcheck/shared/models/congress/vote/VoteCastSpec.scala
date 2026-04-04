package repcheck.shared.models.congress.vote

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class VoteCastSpec extends AnyFlatSpec with Matchers {

  "VoteCast.fromString" should "parse canonical values" in {
    VoteCast.fromString("Yea") shouldBe Right(VoteCast.Yea)
    VoteCast.fromString("Nay") shouldBe Right(VoteCast.Nay)
    VoteCast.fromString("Present") shouldBe Right(VoteCast.Present)
    VoteCast.fromString("Not Voting") shouldBe Right(VoteCast.NotVoting)
    VoteCast.fromString("Absent") shouldBe Right(VoteCast.Absent)
  }

  it should "accept alias 'Aye' for Yea" in {
    VoteCast.fromString("Aye") shouldBe Right(VoteCast.Yea)
  }

  it should "accept alias 'Yes' for Yea" in {
    VoteCast.fromString("Yes") shouldBe Right(VoteCast.Yea)
  }

  it should "accept alias 'No' for Nay" in {
    VoteCast.fromString("No") shouldBe Right(VoteCast.Nay)
  }

  it should "accept alias 'NotVoting' for NotVoting" in {
    VoteCast.fromString("NotVoting") shouldBe Right(VoteCast.NotVoting)
  }

  it should "be case-insensitive" in {
    VoteCast.fromString("YEA") shouldBe Right(VoteCast.Yea)
    VoteCast.fromString("yea") shouldBe Right(VoteCast.Yea)
    VoteCast.fromString("NAY") shouldBe Right(VoteCast.Nay)
    VoteCast.fromString("absent") shouldBe Right(VoteCast.Absent)
  }

  it should "return Left for unknown values" in {
    val result = VoteCast.fromString("Maybe")
    result.isLeft shouldBe true
  }

  "VoteCast Circe codec" should "round-trip values" in {
    VoteCast.values.foreach { vc =>
      val json    = vc.asJson
      val decoded = json.as[VoteCast]
      decoded shouldBe Right(vc)
    }
  }

}
