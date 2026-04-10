package repcheck.shared.models.congress.common

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UsStateSpec extends AnyFlatSpec with Matchers {

  "UsState.fromString" should "parse full state names" in {
    val _ = UsState.fromString("Vermont") shouldBe Right(UsState.Vermont)
    val _ = UsState.fromString("California") shouldBe Right(UsState.California)
    UsState.fromString("New York") shouldBe Right(UsState.NewYork)
  }

  it should "parse 2-letter state codes" in {
    val _ = UsState.fromString("VT") shouldBe Right(UsState.Vermont)
    val _ = UsState.fromString("CA") shouldBe Right(UsState.California)
    UsState.fromString("NY") shouldBe Right(UsState.NewYork)
  }

  it should "be case-insensitive" in {
    val _ = UsState.fromString("vermont") shouldBe Right(UsState.Vermont)
    val _ = UsState.fromString("CALIFORNIA") shouldBe Right(UsState.California)
    UsState.fromString("vt") shouldBe Right(UsState.Vermont)
  }

  it should "trim whitespace" in {
    UsState.fromString("  Vermont  ") shouldBe Right(UsState.Vermont)
  }

  it should "parse territories" in {
    val _ = UsState.fromString("District of Columbia") shouldBe Right(UsState.DistrictOfColumbia)
    val _ = UsState.fromString("DC") shouldBe Right(UsState.DistrictOfColumbia)
    val _ = UsState.fromString("Puerto Rico") shouldBe Right(UsState.PuertoRico)
    val _ = UsState.fromString("PR") shouldBe Right(UsState.PuertoRico)
    val _ = UsState.fromString("Guam") shouldBe Right(UsState.Guam)
    val _ = UsState.fromString("American Samoa") shouldBe Right(UsState.AmericanSamoa)
    val _ = UsState.fromString("Virgin Islands") shouldBe Right(UsState.USVirginIslands)
    UsState.fromString("Northern Mariana Islands") shouldBe Right(UsState.NorthernMarianaIsles)
  }

  it should "return Left for unknown values" in {
    val result = UsState.fromString("Atlantis")
    val _      = result.isLeft shouldBe true
    result.left.map(_.getMessage.contains("Atlantis")) shouldBe Left(true)
  }

  it should "parse all 50 states" in {
    UsState.values.foreach { s =>
      val _ = UsState.fromString(s.fullName) shouldBe Right(s)
      UsState.fromString(s.code) shouldBe Right(s)
    }
  }

  "UsState Circe codec" should "round-trip values" in {
    UsState.values.foreach { s =>
      val json    = s.asJson
      val decoded = json.as[UsState]
      decoded shouldBe Right(s)
    }
  }

  it should "encode to full name" in {
    UsState.Vermont.asJson.noSpaces shouldBe "\"Vermont\""
  }

}
