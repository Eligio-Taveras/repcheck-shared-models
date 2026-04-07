package repcheck.shared.models.congress.bill

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ChangeTypeSpec extends AnyFlatSpec with Matchers {

  "ChangeType.fromString" should "parse all canonical values" in {
    val _ = ChangeType.fromString("Added") shouldBe Right(ChangeType.Added)
    val _ = ChangeType.fromString("Removed") shouldBe Right(ChangeType.Removed)
    val _ = ChangeType.fromString("Modified") shouldBe Right(ChangeType.Modified)
    ChangeType.fromString("Renumbered") shouldBe Right(ChangeType.Renumbered)
  }

  it should "be case-insensitive" in {
    val _ = ChangeType.fromString("ADDED") shouldBe Right(ChangeType.Added)
    val _ = ChangeType.fromString("added") shouldBe Right(ChangeType.Added)
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

  it should "encode to lowercase" in {
    val _ = ChangeType.Added.asJson.noSpaces shouldBe """"added""""
    val _ = ChangeType.Removed.asJson.noSpaces shouldBe """"removed""""
    val _ = ChangeType.Modified.asJson.noSpaces shouldBe """"modified""""
    ChangeType.Renumbered.asJson.noSpaces shouldBe """"renumbered""""
  }

}
