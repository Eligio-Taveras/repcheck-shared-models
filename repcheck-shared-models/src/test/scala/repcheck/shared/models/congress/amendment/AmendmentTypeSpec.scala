package repcheck.shared.models.congress.amendment

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AmendmentTypeSpec extends AnyFlatSpec with Matchers {

  "AmendmentType.fromString" should "parse all canonical values" in {
    val _ = AmendmentType.fromString("HAMDT") shouldBe Right(AmendmentType.HAMDT)
    val _ = AmendmentType.fromString("SAMDT") shouldBe Right(AmendmentType.SAMDT)
    AmendmentType.fromString("SUAMDT") shouldBe Right(AmendmentType.SUAMDT)
  }

  it should "be case-insensitive" in {
    val _ = AmendmentType.fromString("hamdt") shouldBe Right(AmendmentType.HAMDT)
    AmendmentType.fromString("Samdt") shouldBe Right(AmendmentType.SAMDT)
  }

  it should "return Left for unknown values" in {
    val result = AmendmentType.fromString("UNKNOWN")
    result.isLeft shouldBe true
  }

  "AmendmentType Circe codec" should "round-trip values" in {
    AmendmentType.values.foreach { at =>
      val json    = at.asJson
      val decoded = json.as[AmendmentType]
      decoded shouldBe Right(at)
    }
  }

}
