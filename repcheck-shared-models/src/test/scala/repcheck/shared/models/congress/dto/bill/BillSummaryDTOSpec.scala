package repcheck.shared.models.congress.dto.bill

import io.circe.parser.decode

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BillSummaryDTOSpec extends AnyFlatSpec with Matchers {

  "BillSummaryDTO.decoder" should "decode a typical /summaries response entry with bill reference" in {
    val json =
      """
        |{
        |  "actionDate": "2022-02-18",
        |  "actionDesc": "Passed Senate",
        |  "text": "<p><strong>COVID-19 Medical Production Act</strong></p><p>This bill provides...</p>",
        |  "updateDate": "2022-02-18T16:38:41Z",
        |  "versionCode": "55",
        |  "bill": {
        |    "congress": 117,
        |    "type": "HR",
        |    "number": 3076
        |  }
        |}
        |""".stripMargin

    val result = decode[BillSummaryDTO](json)
    val _      = result.isRight shouldBe true

    val dto = result.toOption.get
    val _   = dto.actionDate shouldBe Some("2022-02-18")
    val _   = dto.actionDesc shouldBe Some("Passed Senate")
    val _   = dto.text shouldBe defined
    val _   = dto.updateDate shouldBe "2022-02-18T16:38:41Z"
    val _   = dto.versionCode shouldBe "55"
    val _   = dto.bill shouldBe Some(BillReferenceDTO(congress = 117, billType = "HR", number = 3076L))
    dto.bill.get.naturalKey shouldBe "117-HR-3076"
  }

  it should "decode an entry whose bill reference is absent (bill-scoped /bill/{c}/{t}/{n}/summaries response)" in {
    // The bill-scoped endpoint omits the `bill` reference because the bill is implied by the URL.
    val json =
      """
        |{
        |  "actionDate": "2022-01-15",
        |  "actionDesc": "Introduced in House",
        |  "text": "<p>This bill...</p>",
        |  "updateDate": "2022-01-15T10:00:00Z",
        |  "versionCode": "00"
        |}
        |""".stripMargin

    val dto = decode[BillSummaryDTO](json).toOption.get
    val _   = dto.bill shouldBe None
    dto.versionCode shouldBe "00"
  }

  it should "tolerate missing optional fields (actionDate, actionDesc, text)" in {
    val json =
      """
        |{
        |  "updateDate": "2024-06-01T00:00:00Z",
        |  "versionCode": "70"
        |}
        |""".stripMargin

    val dto = decode[BillSummaryDTO](json).toOption.get
    val _   = dto.actionDate shouldBe None
    val _   = dto.actionDesc shouldBe None
    val _   = dto.text shouldBe None
    val _   = dto.updateDate shouldBe "2024-06-01T00:00:00Z"
    dto.versionCode shouldBe "70"
  }

  it should "fail decoding if updateDate is missing (required for ordering)" in {
    val json =
      """
        |{
        |  "actionDate": "2022-02-18",
        |  "versionCode": "55"
        |}
        |""".stripMargin

    decode[BillSummaryDTO](json).isLeft shouldBe true
  }

  it should "fail decoding if versionCode is missing (required for stage classification)" in {
    val json =
      """
        |{
        |  "updateDate": "2022-02-18T16:38:41Z"
        |}
        |""".stripMargin

    decode[BillSummaryDTO](json).isLeft shouldBe true
  }

  "BillReferenceDTO.naturalKey" should "uppercase the bill type segment to match BillConversions.buildBillNaturalKey" in {
    val ref = BillReferenceDTO(congress = 119, billType = "hr", number = 30L)
    ref.naturalKey shouldBe "119-HR-30"
  }

  it should "round-trip through Circe Decoder for a representative payload" in {
    val json   = """{"congress": 118, "type": "S", "number": 42}"""
    val result = decode[BillReferenceDTO](json)
    val _      = result shouldBe Right(BillReferenceDTO(congress = 118, billType = "S", number = 42L))
    result.toOption.get.naturalKey shouldBe "118-S-42"
  }

}
