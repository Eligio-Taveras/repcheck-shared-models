package repcheck.shared.models.llm

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PorkTypeSpec extends AnyFlatSpec with Matchers {

  "PorkType.fromString" should "parse all values" in {
    PorkType.fromString("Earmark") shouldBe Right(PorkType.Earmark)
    PorkType.fromString("Rider") shouldBe Right(PorkType.Rider)
    PorkType.fromString("UnrelatedProvision") shouldBe Right(PorkType.UnrelatedProvision)
  }

  it should "have exactly 3 values" in {
    PorkType.values.length shouldBe 3
  }

  it should "be case-insensitive" in {
    PorkType.fromString("EARMARK") shouldBe Right(PorkType.Earmark)
    PorkType.fromString("earmark") shouldBe Right(PorkType.Earmark)
    PorkType.fromString("rider") shouldBe Right(PorkType.Rider)
  }

  it should "accept aliases for UnrelatedProvision" in {
    PorkType.fromString("unrelated_provision") shouldBe Right(PorkType.UnrelatedProvision)
    PorkType.fromString("unrelated-provision") shouldBe Right(PorkType.UnrelatedProvision)
    PorkType.fromString("UNRELATED_PROVISION") shouldBe Right(PorkType.UnrelatedProvision)
    PorkType.fromString("UNRELATED-PROVISION") shouldBe Right(PorkType.UnrelatedProvision)
  }

  it should "return Left for unknown values" in {
    PorkType.fromString("Bribery").isLeft shouldBe true
  }

  "PorkType Circe codec" should "round-trip all values" in {
    PorkType.values.foreach(pt => pt.asJson.as[PorkType] shouldBe Right(pt))
  }

  it should "serialize to lowercase" in {
    PorkType.Earmark.asJson.asString shouldBe Some("earmark")
    PorkType.Rider.asJson.asString shouldBe Some("rider")
    PorkType.UnrelatedProvision.asJson.asString shouldBe Some("unrelatedprovision")
  }

}
