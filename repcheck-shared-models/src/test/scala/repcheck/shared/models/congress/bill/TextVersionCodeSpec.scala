package repcheck.shared.models.congress.bill

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TextVersionCodeSpec extends AnyFlatSpec with Matchers {

  "TextVersionCode.fromString" should "parse all 11 code values" in {
    val _ = TextVersionCode.fromString("IH") shouldBe Right(TextVersionCode.IH)
    val _ = TextVersionCode.fromString("IS") shouldBe Right(TextVersionCode.IS)
    val _ = TextVersionCode.fromString("RH") shouldBe Right(TextVersionCode.RH)
    val _ = TextVersionCode.fromString("RS") shouldBe Right(TextVersionCode.RS)
    val _ = TextVersionCode.fromString("RFS") shouldBe Right(TextVersionCode.RFS)
    val _ = TextVersionCode.fromString("RFH") shouldBe Right(TextVersionCode.RFH)
    val _ = TextVersionCode.fromString("EH") shouldBe Right(TextVersionCode.EH)
    val _ = TextVersionCode.fromString("ES") shouldBe Right(TextVersionCode.ES)
    val _ = TextVersionCode.fromString("ENR") shouldBe Right(TextVersionCode.ENR)
    val _ = TextVersionCode.fromString("CPH") shouldBe Right(TextVersionCode.CPH)
    TextVersionCode.fromString("CPS") shouldBe Right(TextVersionCode.CPS)
  }

  it should "accept full name aliases" in {
    val _ = TextVersionCode.fromString("Introduced in House") shouldBe Right(TextVersionCode.IH)
    val _ = TextVersionCode.fromString("Introduced in Senate") shouldBe Right(TextVersionCode.IS)
    val _ = TextVersionCode.fromString("Reported in House") shouldBe Right(TextVersionCode.RH)
    val _ = TextVersionCode.fromString("Reported in Senate") shouldBe Right(TextVersionCode.RS)
    val _ = TextVersionCode.fromString("Referred to Senate") shouldBe Right(TextVersionCode.RFS)
    val _ = TextVersionCode.fromString("Referred to House") shouldBe Right(TextVersionCode.RFH)
    val _ = TextVersionCode.fromString("Engrossed in House") shouldBe Right(TextVersionCode.EH)
    val _ = TextVersionCode.fromString("Engrossed in Senate") shouldBe Right(TextVersionCode.ES)
    val _ = TextVersionCode.fromString("Enrolled Bill") shouldBe Right(TextVersionCode.ENR)
    val _ = TextVersionCode.fromString("Committee Print (House)") shouldBe Right(TextVersionCode.CPH)
    TextVersionCode.fromString("Committee Print (Senate)") shouldBe Right(TextVersionCode.CPS)
  }

  it should "be case-insensitive for codes" in {
    val _ = TextVersionCode.fromString("ih") shouldBe Right(TextVersionCode.IH)
    val _ = TextVersionCode.fromString("Ih") shouldBe Right(TextVersionCode.IH)
    TextVersionCode.fromString("enr") shouldBe Right(TextVersionCode.ENR)
  }

  it should "be case-insensitive for full names" in {
    val _ = TextVersionCode.fromString("INTRODUCED IN HOUSE") shouldBe Right(TextVersionCode.IH)
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
