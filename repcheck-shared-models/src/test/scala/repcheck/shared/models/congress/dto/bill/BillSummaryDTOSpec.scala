package repcheck.shared.models.congress.dto.bill

import io.circe.parser.decode

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BillSummaryDTOSpec extends AnyFlatSpec with Matchers {

  /**
   * Pattern-match an `Either[L, R]` to extract the Right value or fail the test. Uses pattern matching rather than
   * `Either.toOption.get` to comply with WartRemover's `OptionPartial` rule.
   */
  private def expectRight[L, R](either: Either[L, R]): R = either match {
    case Right(value) => value
    case Left(err)    => fail(s"Expected Right but got Left($err)")
  }

  /**
   * Pattern-match an `Option[T]` to extract the Some value or fail the test. WartRemover's `OptionPartial` rule
   * disallows `.get` directly; this helper centralizes the failure message.
   */
  private def expectSome[T](opt: Option[T], context: String): T = opt match {
    case Some(value) => value
    case None        => fail(s"Expected Some($context) but got None")
  }

  "BillSummaryDTO.decoder" should "decode a global /summaries response entry with bill reference" in {
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

    val dto = expectRight(decode[BillSummaryDTO](json))
    val _   = dto.actionDate shouldBe Some("2022-02-18")
    val _   = dto.actionDesc shouldBe Some("Passed Senate")
    val _   = dto.text shouldBe defined
    val _   = dto.updateDate shouldBe Some("2022-02-18T16:38:41Z")
    val _   = dto.versionCode shouldBe Some("55")
    val _   = dto.bill shouldBe Some(BillReferenceDTO(congress = 117, billType = "HR", number = 3076L))

    val ref = expectSome(dto.bill, "decoded bill reference")
    ref.naturalKey shouldBe "117-HR-3076"
  }

  it should "decode a bill-scoped /bill/{c}/{t}/{n}/summaries entry where bill ref is absent" in {
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

    val dto = expectRight(decode[BillSummaryDTO](json))
    val _   = dto.bill shouldBe None
    dto.versionCode shouldBe Some("00")
  }

  it should "tolerate every field missing (defensive, matches original BillDetailDTO.summaries usage)" in {
    val dto = expectRight(decode[BillSummaryDTO]("""{}"""))
    val _   = dto.actionDate shouldBe None
    val _   = dto.actionDesc shouldBe None
    val _   = dto.text shouldBe None
    val _   = dto.updateDate shouldBe None
    val _   = dto.versionCode shouldBe None
    dto.bill shouldBe None
  }

  it should "default the bill reference to None when missing" in {
    val json =
      """
        |{
        |  "updateDate": "2024-06-01T00:00:00Z",
        |  "versionCode": "70"
        |}
        |""".stripMargin

    val dto = expectRight(decode[BillSummaryDTO](json))
    val _   = dto.bill shouldBe None
    dto.versionCode shouldBe Some("70")
  }

  "BillReferenceDTO.naturalKey" should "uppercase the bill type segment to match BillConversions.buildBillNaturalKey" in {
    val ref = BillReferenceDTO(congress = 119, billType = "hr", number = 30L)
    ref.naturalKey shouldBe "119-HR-30"
  }

  it should "round-trip through Circe Decoder for a representative payload" in {
    val json   = """{"congress": 118, "type": "S", "number": 42}"""
    val result = decode[BillReferenceDTO](json)
    val _      = result shouldBe Right(BillReferenceDTO(congress = 118, billType = "S", number = 42L))
    val ref    = expectRight(result)
    ref.naturalKey shouldBe "118-S-42"
  }

  it should "round-trip through Circe Encoder using the API field name 'type' (not 'billType')" in {
    import io.circe.syntax._
    val ref = BillReferenceDTO(congress = 117, billType = "HR", number = 3076L)
    val out = ref.asJson.noSpaces
    val _   = out should include("\"congress\":117")
    val _   = out should include("\"type\":\"HR\"")
    out should include("\"number\":3076")
  }

}
