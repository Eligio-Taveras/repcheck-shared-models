package repcheck.shared.models.congress.bill

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TextVersionCodeSpec extends AnyFlatSpec with Matchers {

  "TextVersionCode.fromString" should "parse all 11 code values" in {
    TextVersionCode.fromString("IH") shouldBe Right(TextVersionCode.IH)
    TextVersionCode.fromString("IS") shouldBe Right(TextVersionCode.IS)
    TextVersionCode.fromString("RH") shouldBe Right(TextVersionCode.RH)
    TextVersionCode.fromString("RS") shouldBe Right(TextVersionCode.RS)
    TextVersionCode.fromString("RFS") shouldBe Right(TextVersionCode.RFS)
    TextVersionCode.fromString("RFH") shouldBe Right(TextVersionCode.RFH)
    TextVersionCode.fromString("EH") shouldBe Right(TextVersionCode.EH)
    TextVersionCode.fromString("ES") shouldBe Right(TextVersionCode.ES)
    TextVersionCode.fromString("ENR") shouldBe Right(TextVersionCode.ENR)
    TextVersionCode.fromString("CPH") shouldBe Right(TextVersionCode.CPH)
    TextVersionCode.fromString("CPS") shouldBe Right(TextVersionCode.CPS)
  }

  it should "accept full name aliases" in {
    TextVersionCode.fromString("Introduced in House") shouldBe Right(TextVersionCode.IH)
    TextVersionCode.fromString("Introduced in Senate") shouldBe Right(TextVersionCode.IS)
    TextVersionCode.fromString("Reported in House") shouldBe Right(TextVersionCode.RH)
    TextVersionCode.fromString("Reported in Senate") shouldBe Right(TextVersionCode.RS)
    TextVersionCode.fromString("Referred to Senate") shouldBe Right(TextVersionCode.RFS)
    TextVersionCode.fromString("Referred to House") shouldBe Right(TextVersionCode.RFH)
    TextVersionCode.fromString("Engrossed in House") shouldBe Right(TextVersionCode.EH)
    TextVersionCode.fromString("Engrossed in Senate") shouldBe Right(TextVersionCode.ES)
    TextVersionCode.fromString("Enrolled Bill") shouldBe Right(TextVersionCode.ENR)
    TextVersionCode.fromString("Committee Print (House)") shouldBe Right(TextVersionCode.CPH)
    TextVersionCode.fromString("Committee Print (Senate)") shouldBe Right(TextVersionCode.CPS)
  }

  it should "be case-insensitive for codes" in {
    TextVersionCode.fromString("ih") shouldBe Right(TextVersionCode.IH)
    TextVersionCode.fromString("Ih") shouldBe Right(TextVersionCode.IH)
    TextVersionCode.fromString("enr") shouldBe Right(TextVersionCode.ENR)
  }

  it should "be case-insensitive for full names" in {
    TextVersionCode.fromString("INTRODUCED IN HOUSE") shouldBe Right(TextVersionCode.IH)
    TextVersionCode.fromString("enrolled bill") shouldBe Right(TextVersionCode.ENR)
  }

  it should "return Left for unknown values" in {
    val result = TextVersionCode.fromString("UNKNOWN")
    result.isLeft shouldBe true
  }

  "TextVersionCode Circe codec" should "round-trip values" in {
    TextVersionCode.values.foreach { tvc =>
      val json    = tvc.asJson
      val decoded = json.as[TextVersionCode]
      decoded shouldBe Right(tvc)
    }
  }

}
