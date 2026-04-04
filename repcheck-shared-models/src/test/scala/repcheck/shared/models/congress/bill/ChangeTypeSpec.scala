package repcheck.shared.models.congress.bill

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ChangeTypeSpec extends AnyFlatSpec with Matchers {

  "ChangeType.fromString" should "parse all canonical values" in {
    ChangeType.fromString("Added") shouldBe Right(ChangeType.Added)
    ChangeType.fromString("Removed") shouldBe Right(ChangeType.Removed)
    ChangeType.fromString("Modified") shouldBe Right(ChangeType.Modified)
    ChangeType.fromString("Renumbered") shouldBe Right(ChangeType.Renumbered)
  }

  it should "be case-insensitive" in {
    ChangeType.fromString("ADDED") shouldBe Right(ChangeType.Added)
    ChangeType.fromString("added") shouldBe Right(ChangeType.Added)
    ChangeType.fromString("modified") shouldBe Right(ChangeType.Modified)
  }

  it should "return Left for unknown values" in {
    val result = ChangeType.fromString("Replaced")
    result.isLeft shouldBe true
  }

  "ChangeType Circe codec" should "round-trip values" in {
    ChangeType.values.foreach { ct =>
      val json    = ct.asJson
      val decoded = json.as[ChangeType]
      decoded shouldBe Right(ct)
    }
  }

}
