package repcheck.shared.models.congress.vote

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class VoteCastSpec extends AnyFlatSpec with Matchers {

  "VoteCast.fromString" should "parse canonical values" in {
    val _ = VoteCast.fromString("Yea") shouldBe Right(VoteCast.Yea)
    val _ = VoteCast.fromString("Nay") shouldBe Right(VoteCast.Nay)
    val _ = VoteCast.fromString("Present") shouldBe Right(VoteCast.Present)
    val _ = VoteCast.fromString("Not Voting") shouldBe Right(VoteCast.NotVoting)
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
    val _ = VoteCast.fromString("YEA") shouldBe Right(VoteCast.Yea)
    val _ = VoteCast.fromString("yea") shouldBe Right(VoteCast.Yea)
    val _ = VoteCast.fromString("NAY") shouldBe Right(VoteCast.Nay)
    VoteCast.fromString("absent") shouldBe Right(VoteCast.Absent)
  }

  it should "return Left for unknown values" in {
    val result = VoteCast.fromString("Maybe")
    result.isLeft shouldBe true
  }

  it should "accept 'Candidate' for officer-election votes" in {
    VoteCast.fromString("Candidate") shouldBe Right(VoteCast.Candidate)
  }

  it should "alias Senate impeachment-trial 'Guilty' to Yea" in {
    // Senate impeachment trials emit "Guilty" / "Not Guilty" in <vote_cast>. The Senate's question
    // is "should we convict?", so Guilty IS Yea on conviction. Keeps alignment-score math uniform
    // without a separate enum case. Surfaced live on 117-Senate-1-59 (Trump 2nd impeachment
    // verdict, Feb 2021).
    val _ = VoteCast.fromString("Guilty") shouldBe Right(VoteCast.Yea)
    val _ = VoteCast.fromString("GUILTY") shouldBe Right(VoteCast.Yea)
    VoteCast.fromString("guilty") shouldBe Right(VoteCast.Yea)
  }

  it should "alias Senate impeachment-trial 'Not Guilty' to Nay" in {
    val _ = VoteCast.fromString("Not Guilty") shouldBe Right(VoteCast.Nay)
    val _ = VoteCast.fromString("NOT GUILTY") shouldBe Right(VoteCast.Nay)
    VoteCast.fromString("not guilty") shouldBe Right(VoteCast.Nay)
  }

  it should "NOT accept candidate names like 'Jeffries' as direct VoteCast values" in {
    // Candidate-name handling is VoteType-aware and happens in VoteConversions.parseVoteCast,
    // not in VoteCast.fromString — keep the enum parser strict so a future bug can't silently
    // absorb a candidate name on a normal legislative vote.
    val _ = VoteCast.fromString("Jeffries").isLeft shouldBe true
    VoteCast.fromString("Johnson (LA)").isLeft shouldBe true
  }

  "VoteCast Circe codec" should "round-trip values" in {
    VoteCast.values.foreach { vc =>
      val json    = vc.asJson
      val decoded = json.as[VoteCast]
      decoded shouldBe Right(vc)
    }
  }

}
