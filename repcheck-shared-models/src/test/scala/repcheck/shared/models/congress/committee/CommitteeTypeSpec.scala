package repcheck.shared.models.congress.committee

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommitteeTypeSpec extends AnyFlatSpec with Matchers {

  "CommitteeType.fromString" should "parse all canonical values" in {
    val _ = CommitteeType.fromString("Standing") shouldBe Right(CommitteeType.Standing)
    val _ = CommitteeType.fromString("Special") shouldBe Right(CommitteeType.Special)
    val _ = CommitteeType.fromString("Select") shouldBe Right(CommitteeType.Select)
    val _ = CommitteeType.fromString("Joint") shouldBe Right(CommitteeType.Joint)
    CommitteeType.fromString("Subcommittee") shouldBe Right(CommitteeType.Subcommittee)
  }

  it should "be case-insensitive" in {
    val _ = CommitteeType.fromString("STANDING") shouldBe Right(CommitteeType.Standing)
    val _ = CommitteeType.fromString("standing") shouldBe Right(CommitteeType.Standing)
    CommitteeType.fromString("joint") shouldBe Right(CommitteeType.Joint)
  }

  it should "return Left for unknown values" in {
    val result = CommitteeType.fromString("Temporary")
    result.isLeft shouldBe true
  }

  "CommitteeType Circe codec" should "round-trip values" in {
    CommitteeType.values.foreach { ct =>
      val json    = ct.asJson
      val decoded = json.as[CommitteeType]
      decoded shouldBe Right(ct)
    }
  }

}
