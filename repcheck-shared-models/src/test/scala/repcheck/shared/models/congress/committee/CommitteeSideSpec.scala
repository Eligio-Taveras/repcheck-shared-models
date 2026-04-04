package repcheck.shared.models.congress.committee

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommitteeSideSpec extends AnyFlatSpec with Matchers {

  "CommitteeSide.fromString" should "parse canonical values" in {
    CommitteeSide.fromString("Majority") shouldBe Right(CommitteeSide.Majority)
    CommitteeSide.fromString("Minority") shouldBe Right(CommitteeSide.Minority)
  }

  it should "accept alias 'M' for Majority" in {
    CommitteeSide.fromString("M") shouldBe Right(CommitteeSide.Majority)
  }

  it should "be case-insensitive" in {
    CommitteeSide.fromString("majority") shouldBe Right(CommitteeSide.Majority)
    CommitteeSide.fromString("MAJORITY") shouldBe Right(CommitteeSide.Majority)
    CommitteeSide.fromString("minority") shouldBe Right(CommitteeSide.Minority)
    CommitteeSide.fromString("MINORITY") shouldBe Right(CommitteeSide.Minority)
  }

  it should "return Left for unknown values" in {
    val result = CommitteeSide.fromString("Neutral")
    result.isLeft shouldBe true
  }

  "CommitteeSide Circe codec" should "round-trip values" in {
    CommitteeSide.values.foreach { cs =>
      val json    = cs.asJson
      val decoded = json.as[CommitteeSide]
      decoded shouldBe Right(cs)
    }
  }

}
