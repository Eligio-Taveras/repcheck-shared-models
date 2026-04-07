package repcheck.shared.models.congress.committee

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommitteePositionSpec extends AnyFlatSpec with Matchers {

  "CommitteePosition.fromString" should "parse canonical values" in {
    val _ = CommitteePosition.fromString("Chairman") shouldBe Right(CommitteePosition.Chairman)
    val _ = CommitteePosition.fromString("Ranking Member") shouldBe Right(CommitteePosition.RankingMember)
    val _ = CommitteePosition.fromString("Vice Chairman") shouldBe Right(CommitteePosition.ViceChairman)
    CommitteePosition.fromString("Member") shouldBe Right(CommitteePosition.Member)
  }

  it should "accept alias 'Chair' for Chairman" in {
    CommitteePosition.fromString("Chair") shouldBe Right(CommitteePosition.Chairman)
  }

  it should "accept alias 'Chairwoman' for Chairman" in {
    CommitteePosition.fromString("Chairwoman") shouldBe Right(CommitteePosition.Chairman)
  }

  it should "accept alias 'Chairperson' for Chairman" in {
    CommitteePosition.fromString("Chairperson") shouldBe Right(CommitteePosition.Chairman)
  }

  it should "accept alias 'Ranking' for RankingMember" in {
    CommitteePosition.fromString("Ranking") shouldBe Right(CommitteePosition.RankingMember)
  }

  it should "accept alias 'RankingMember' for RankingMember" in {
    CommitteePosition.fromString("RankingMember") shouldBe Right(CommitteePosition.RankingMember)
  }

  it should "accept alias 'Vice Chair' for ViceChairman" in {
    CommitteePosition.fromString("Vice Chair") shouldBe Right(CommitteePosition.ViceChairman)
  }

  it should "accept alias 'Vice Chairwoman' for ViceChairman" in {
    CommitteePosition.fromString("Vice Chairwoman") shouldBe Right(CommitteePosition.ViceChairman)
  }

  it should "be case-insensitive" in {
    val _ = CommitteePosition.fromString("chairman") shouldBe Right(CommitteePosition.Chairman)
    CommitteePosition.fromString("MEMBER") shouldBe Right(CommitteePosition.Member)
  }

  it should "return Left for unknown values" in {
    val result = CommitteePosition.fromString("President")
    result.isLeft shouldBe true
  }

  "CommitteePosition.weight" should "be 1.0 for Chairman" in {
    CommitteePosition.Chairman.weight shouldBe 1.0
  }

  it should "be 0.7 for RankingMember" in {
    CommitteePosition.RankingMember.weight shouldBe 0.7
  }

  it should "be 0.6 for ViceChairman" in {
    CommitteePosition.ViceChairman.weight shouldBe 0.6
  }

  it should "be 0.4 for Member" in {
    CommitteePosition.Member.weight shouldBe 0.4
  }

  "CommitteePosition Circe codec" should "round-trip values" in {
    CommitteePosition.values.foreach { cp =>
      val json    = cp.asJson
      val decoded = json.as[CommitteePosition]
      decoded shouldBe Right(cp)
    }
  }

}
