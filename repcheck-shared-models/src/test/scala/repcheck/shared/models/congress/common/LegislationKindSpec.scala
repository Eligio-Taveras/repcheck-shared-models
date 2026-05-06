package repcheck.shared.models.congress.common

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LegislationKindSpec extends AnyFlatSpec with Matchers {

  "LegislationKind.fromString" should "parse all canonical values" in {
    val _ = LegislationKind.fromString("BILL") shouldBe Right(LegislationKind.BILL)
    LegislationKind.fromString("AMENDMENT") shouldBe Right(LegislationKind.AMENDMENT)
  }

  it should "be case-insensitive" in {
    val _ = LegislationKind.fromString("bill") shouldBe Right(LegislationKind.BILL)
    val _ = LegislationKind.fromString("Bill") shouldBe Right(LegislationKind.BILL)
    val _ = LegislationKind.fromString("amendment") shouldBe Right(LegislationKind.AMENDMENT)
    LegislationKind.fromString("AMendment") shouldBe Right(LegislationKind.AMENDMENT)
  }

  it should "return Left UnrecognizedLegislationKind for unknown values" in {
    val result = LegislationKind.fromString("UNKNOWN")
    val _      = result.isLeft shouldBe true
    val _      = result.left.map(_.value) shouldBe Left("UNKNOWN")
    result.left.map(_.getMessage) should matchPattern {
      case Left(msg: String) if msg.contains("UNKNOWN") && msg.contains("BILL") && msg.contains("AMENDMENT") =>
    }
  }

  it should "return Left for empty string" in {
    LegislationKind.fromString("").isLeft shouldBe true
  }

  "LegislationKind apiValue" should "be uppercase enum name" in {
    val _ = LegislationKind.BILL.apiValue shouldBe "BILL"
    LegislationKind.AMENDMENT.apiValue shouldBe "AMENDMENT"
  }

  "LegislationKind Circe codec" should "round-trip all values" in {
    LegislationKind.values.foreach { k =>
      val json    = k.asJson
      val decoded = json.as[LegislationKind]
      decoded shouldBe Right(k)
    }
  }

  it should "encode to apiValue (uppercase)" in {
    val _ = LegislationKind.BILL.asJson.noSpaces shouldBe """"BILL""""
    LegislationKind.AMENDMENT.asJson.noSpaces shouldBe """"AMENDMENT""""
  }

  it should "decode from JSON string" in {
    val _ = decode[LegislationKind](""""BILL"""") shouldBe Right(LegislationKind.BILL)
    val _ = decode[LegislationKind](""""bill"""") shouldBe Right(LegislationKind.BILL)
    decode[LegislationKind](""""AMENDMENT"""") shouldBe Right(LegislationKind.AMENDMENT)
  }

  it should "fail to decode unknown JSON string" in {
    val result = decode[LegislationKind](""""TREATY"""")
    val _      = result.isLeft shouldBe true
    result.left.map(_.getMessage.contains("TREATY")) shouldBe Left(true)
  }

}
