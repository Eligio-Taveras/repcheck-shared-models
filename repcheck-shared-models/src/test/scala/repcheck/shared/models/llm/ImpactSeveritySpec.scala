package repcheck.shared.models.llm

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ImpactSeveritySpec extends AnyFlatSpec with Matchers {

  "ImpactSeverity.fromString" should "parse all values" in {
    val _ = ImpactSeverity.fromString("High") shouldBe Right(ImpactSeverity.High)
    val _ = ImpactSeverity.fromString("Medium") shouldBe Right(ImpactSeverity.Medium)
    ImpactSeverity.fromString("Low") shouldBe Right(ImpactSeverity.Low)
  }

  it should "have exactly 3 values" in {
    ImpactSeverity.values.length shouldBe 3
  }

  it should "be case-insensitive" in {
    val _ = ImpactSeverity.fromString("HIGH") shouldBe Right(ImpactSeverity.High)
    val _ = ImpactSeverity.fromString("high") shouldBe Right(ImpactSeverity.High)
    val _ = ImpactSeverity.fromString("medium") shouldBe Right(ImpactSeverity.Medium)
    ImpactSeverity.fromString("LOW") shouldBe Right(ImpactSeverity.Low)
  }

  it should "return Left for unknown values" in {
    ImpactSeverity.fromString("Critical").isLeft shouldBe true
  }

  "ImpactSeverity Circe codec" should "round-trip all values" in {
    ImpactSeverity.values.foreach(is => is.asJson.as[ImpactSeverity] shouldBe Right(is))
  }

  it should "serialize to lowercase" in {
    val _ = ImpactSeverity.High.asJson.asString shouldBe Some("high")
    val _ = ImpactSeverity.Medium.asJson.asString shouldBe Some("medium")
    ImpactSeverity.Low.asJson.asString shouldBe Some("low")
  }

}
