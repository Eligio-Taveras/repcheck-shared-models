package repcheck.shared.models.congress.bill

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TextVersionCodeSpec extends AnyFlatSpec with Matchers {

  "TextVersionCode.fromString" should "parse all 30 code values" in {
    val _ = TextVersionCode.fromString("IH") shouldBe Right(TextVersionCode.IH)
    val _ = TextVersionCode.fromString("IS") shouldBe Right(TextVersionCode.IS)
    val _ = TextVersionCode.fromString("RH") shouldBe Right(TextVersionCode.RH)
    val _ = TextVersionCode.fromString("RS") shouldBe Right(TextVersionCode.RS)
    val _ = TextVersionCode.fromString("RFS") shouldBe Right(TextVersionCode.RFS)
    val _ = TextVersionCode.fromString("RFH") shouldBe Right(TextVersionCode.RFH)
    val _ = TextVersionCode.fromString("EH") shouldBe Right(TextVersionCode.EH)
    val _ = TextVersionCode.fromString("ES") shouldBe Right(TextVersionCode.ES)
    val _ = TextVersionCode.fromString("EAS") shouldBe Right(TextVersionCode.EAS)
    val _ = TextVersionCode.fromString("EAH") shouldBe Right(TextVersionCode.EAH)
    val _ = TextVersionCode.fromString("ENR") shouldBe Right(TextVersionCode.ENR)
    val _ = TextVersionCode.fromString("CPH") shouldBe Right(TextVersionCode.CPH)
    val _ = TextVersionCode.fromString("CPS") shouldBe Right(TextVersionCode.CPS)
    val _ = TextVersionCode.fromString("PCS") shouldBe Right(TextVersionCode.PCS)
    val _ = TextVersionCode.fromString("PCH") shouldBe Right(TextVersionCode.PCH)
    val _ = TextVersionCode.fromString("PL") shouldBe Right(TextVersionCode.PL)
    val _ = TextVersionCode.fromString("PRL") shouldBe Right(TextVersionCode.PRL)
    val _ = TextVersionCode.fromString("RDS") shouldBe Right(TextVersionCode.RDS)
    val _ = TextVersionCode.fromString("RDH") shouldBe Right(TextVersionCode.RDH)
    val _ = TextVersionCode.fromString("RTS") shouldBe Right(TextVersionCode.RTS)
    val _ = TextVersionCode.fromString("RTH") shouldBe Right(TextVersionCode.RTH)
    val _ = TextVersionCode.fromString("ATS") shouldBe Right(TextVersionCode.ATS)
    val _ = TextVersionCode.fromString("ATH") shouldBe Right(TextVersionCode.ATH)
    val _ = TextVersionCode.fromString("PP") shouldBe Right(TextVersionCode.PP)
    val _ = TextVersionCode.fromString("RCH") shouldBe Right(TextVersionCode.RCH)
    val _ = TextVersionCode.fromString("RCS") shouldBe Right(TextVersionCode.RCS)
    val _ = TextVersionCode.fromString("RIS") shouldBe Right(TextVersionCode.RIS)
    val _ = TextVersionCode.fromString("RIH") shouldBe Right(TextVersionCode.RIH)
    val _ = TextVersionCode.fromString("LTH") shouldBe Right(TextVersionCode.LTH)
    TextVersionCode.fromString("LTS") shouldBe Right(TextVersionCode.LTS)
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
    val _ = TextVersionCode.fromString("Engrossed Amendment Senate") shouldBe Right(TextVersionCode.EAS)
    val _ = TextVersionCode.fromString("Engrossed Amendment House") shouldBe Right(TextVersionCode.EAH)
    val _ = TextVersionCode.fromString("Enrolled Bill") shouldBe Right(TextVersionCode.ENR)
    val _ = TextVersionCode.fromString("Committee Print (House)") shouldBe Right(TextVersionCode.CPH)
    val _ = TextVersionCode.fromString("Committee Print (Senate)") shouldBe Right(TextVersionCode.CPS)
    val _ = TextVersionCode.fromString("Placed on Calendar Senate") shouldBe Right(TextVersionCode.PCS)
    val _ = TextVersionCode.fromString("Placed on Calendar House") shouldBe Right(TextVersionCode.PCH)
    val _ = TextVersionCode.fromString("Public Law") shouldBe Right(TextVersionCode.PL)
    val _ = TextVersionCode.fromString("Private Law") shouldBe Right(TextVersionCode.PRL)
    val _ = TextVersionCode.fromString("Received in Senate") shouldBe Right(TextVersionCode.RDS)
    val _ = TextVersionCode.fromString("Received in House") shouldBe Right(TextVersionCode.RDH)
    val _ = TextVersionCode.fromString("Reported to Senate") shouldBe Right(TextVersionCode.RTS)
    val _ = TextVersionCode.fromString("Reported to House") shouldBe Right(TextVersionCode.RTH)
    val _ = TextVersionCode.fromString("Agreed to Senate") shouldBe Right(TextVersionCode.ATS)
    val _ = TextVersionCode.fromString("Agreed to House") shouldBe Right(TextVersionCode.ATH)
    val _ = TextVersionCode.fromString("Printed as Passed") shouldBe Right(TextVersionCode.PP)
    val _ = TextVersionCode.fromString("Reference Change House") shouldBe Right(TextVersionCode.RCH)
    val _ = TextVersionCode.fromString("Reference Change Senate") shouldBe Right(TextVersionCode.RCS)
    val _ = TextVersionCode.fromString("Referral Instructions Senate") shouldBe Right(TextVersionCode.RIS)
    val _ = TextVersionCode.fromString("Referral Instructions House") shouldBe Right(TextVersionCode.RIH)
    val _ = TextVersionCode.fromString("Laid on Table in House") shouldBe Right(TextVersionCode.LTH)
    TextVersionCode.fromString("Laid on Table in Senate") shouldBe Right(TextVersionCode.LTS)
  }

  it should "accept extra alias 'Public Print' as PP (PP's canonical fullName is 'Printed as Passed')" in {
    val _ = TextVersionCode.fromString("Public Print") shouldBe Right(TextVersionCode.PP)
    val _ = TextVersionCode.fromString("PUBLIC PRINT") shouldBe Right(TextVersionCode.PP)
    TextVersionCode.fromString("public print") shouldBe Right(TextVersionCode.PP)
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
