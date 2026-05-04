package repcheck.shared.models.congress.dto.amendment

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.bill.{LatestActionDTO, SponsorDTO}
import repcheck.shared.models.congress.dto.common.PaginationInfoDTO

class AmendmentDTOsSpec extends AnyFlatSpec with Matchers {

  "AmendedBillDTO" should "round-trip" in {
    val dto = AmendedBillDTO(
      congress = Some(118),
      number = Some(1234),
      originChamber = Some("House"),
      originChamberCode = Some("H"),
      title = Some("A bill to do something"),
      billType = Some("hr"),
      url = Some("https://api.congress.gov/v3/bill/118/hr/1234"),
      updateDateIncludingText = Some("2024-03-15T10:00:00Z"),
    )
    decode[AmendedBillDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with all None fields" in {
    decode[AmendedBillDTO]("{}") shouldBe Right(AmendedBillDTO(None, None, None, None, None, None, None, None))
  }

  "AmendedAmendmentDTO" should "round-trip" in {
    val dto = AmendedAmendmentDTO(
      congress = Some(118),
      number = Some("100"),
      amendmentType = Some("SAMDT"),
      description = Some("Parent amendment"),
      purpose = Some("To strike sections"),
      url = Some("https://api.congress.gov/v3/amendment/118/samdt/100"),
    )
    decode[AmendedAmendmentDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with all None fields" in {
    decode[AmendedAmendmentDTO]("{}") shouldBe Right(AmendedAmendmentDTO(None, None, None, None, None, None))
  }

  "AmendmentListItemDTO" should "round-trip" in {
    val dto = AmendmentListItemDTO(
      congress = 118,
      number = "100",
      amendmentType = Some("HAMDT"),
      description = Some("An amendment to HR 1234"),
      latestAction = Some(LatestActionDTO("2024-03-01", "Amendment agreed to")),
      updateDate = Some("2024-03-02"),
      url = Some("https://api.congress.gov/v3/amendment/118/hamdt/100"),
    )
    decode[AmendmentListItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with only required fields" in {
    val json   = """{"congress":118,"number":"50"}"""
    val result = decode[AmendmentListItemDTO](json)
    val _      = result.isRight shouldBe true
    result.map(_.amendmentType) shouldBe Right(None)
  }

  "AmendmentDetailDTO" should "round-trip with all fields" in {
    val dto = AmendmentDetailDTO(
      congress = 118,
      number = "200",
      amendmentType = Some("SAMDT"),
      amendedBill = Some(
        AmendedBillDTO(Some(118), Some(5678), Some("Senate"), Some("S"), Some("Test Bill"), Some("s"), None, None)
      ),
      amendedAmendment = Some(
        AmendedAmendmentDTO(Some(118), Some("100"), Some("SAMDT"), Some("parent"), None, None)
      ),
      chamber = Some("Senate"),
      description = Some("Amendment description"),
      purpose = Some("To improve the bill"),
      sponsors = Some(
        List(SponsorDTO("S000033", Some("Bernard"), Some("Sanders"), None, None, None, Some("I"), Some("VT"), None))
      ),
      submittedDate = Some("2024-02-15"),
      proposedDate = Some("2024-02-16T00:00:00Z"),
      latestAction =
        Some(LatestActionDTO(actionDate = "2024-03-01", text = "Submitted", actionTime = Some("14:30:00"))),
      updateDate = Some("2024-03-15"),
      actions = Some(List(LatestActionDTO("2024-03-01", "Submitted"), LatestActionDTO("2024-03-10", "Agreed to"))),
      textVersions = Some(List("v1", "v2")),
    )
    decode[AmendmentDetailDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with only required fields" in {
    val json   = """{"congress":118,"number":"300"}"""
    val result = decode[AmendmentDetailDTO](json)
    val _      = result.isRight shouldBe true
    val _      = result.map(_.amendedBill) shouldBe Right(None)
    val _      = result.map(_.amendedAmendment) shouldBe Right(None)
    val _      = result.map(_.sponsors) shouldBe Right(None)
    val _      = result.map(_.proposedDate) shouldBe Right(None)
    val _      = result.map(_.actions) shouldBe Right(None)
    result.map(_.textVersions) shouldBe Right(None)
  }

  "AmendmentListResponseDTO" should "round-trip via Circe" in {
    val item = AmendmentListItemDTO(118, "100", None, None, None, None, None)
    val resp = AmendmentListResponseDTO(List(item), Some(PaginationInfoDTO(Some(1), None)))
    decode[AmendmentListResponseDTO](resp.asJson.noSpaces) shouldBe Right(resp)
  }

  it should "combine via Semigroup" in {
    import cats.Semigroup
    val a = AmendmentListResponseDTO(
      List(AmendmentListItemDTO(118, "1", None, None, None, None, None)),
      Some(PaginationInfoDTO(Some(1), Some("p1"))),
    )
    val b = AmendmentListResponseDTO(
      List(AmendmentListItemDTO(118, "2", None, None, None, None, None)),
      Some(PaginationInfoDTO(Some(1), Some("p2"))),
    )
    val combined = Semigroup[AmendmentListResponseDTO].combine(a, b)
    val _        = combined.items should have size 2
    combined.pagination shouldBe b.pagination
  }

  "AmendmentFormatDTO" should "round-trip with realistic CREC URL" in {
    val dto = AmendmentFormatDTO(
      `type` = "HTML",
      url = "https://www.congress.gov/117/crec/2021/08/01/167/136/modified/CREC-2021-08-01-pt1-PgS5255.htm",
    )
    decode[AmendmentFormatDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "fail decoding when type or url is missing" in {
    val _ = decode[AmendmentFormatDTO]("""{"type":"PDF"}""").isLeft shouldBe true
    decode[AmendmentFormatDTO]("""{"url":"https://x"}""").isLeft shouldBe true
  }

  "AmendmentTextItemDTO" should "round-trip with two formats" in {
    val dto = AmendmentTextItemDTO(
      `type` = Some("Submitted"),
      date = Some("2021-08-01T00:00:00Z"),
      formats = List(
        AmendmentFormatDTO(
          "PDF",
          "https://www.congress.gov/117/crec/2021/08/01/167/136/CREC-2021-08-01-pt1-PgS5255.pdf",
        ),
        AmendmentFormatDTO(
          "HTML",
          "https://www.congress.gov/117/crec/2021/08/01/167/136/CREC-2021-08-01-pt1-PgS5255.htm",
        ),
      ),
    )
    decode[AmendmentTextItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with no type, no date, empty formats" in {
    val json = """{"formats":[]}"""
    decode[AmendmentTextItemDTO](json) shouldBe Right(AmendmentTextItemDTO(None, None, Nil))
  }

  "AmendmentTextResponseDTO" should "round-trip with realistic Congress.gov fixture" in {
    val fixture =
      """{
        |  "textVersions": [
        |    {
        |      "type": "Submitted",
        |      "date": "2021-08-01T00:00:00Z",
        |      "formats": [
        |        {"type": "PDF",  "url": "https://www.congress.gov/117/crec/2021/08/01/167/136/CREC-2021-08-01-pt1-PgS5255.pdf"},
        |        {"type": "HTML", "url": "https://www.congress.gov/117/crec/2021/08/01/167/136/CREC-2021-08-01-pt1-PgS5255.htm"}
        |      ]
        |    },
        |    {
        |      "type": "Modified",
        |      "date": "2021-08-02T00:00:00Z",
        |      "formats": [
        |        {"type": "HTML", "url": "https://www.congress.gov/117/crec/2021/08/02/167/137/modified/CREC-2021-08-02-pt1-PgS5300.htm"}
        |      ]
        |    }
        |  ],
        |  "pagination": {"count": 2, "url": null}
        |}""".stripMargin

    val Right(decoded) = decode[AmendmentTextResponseDTO](fixture): @unchecked
    val _              = decoded.textVersions should have size 2
    val _              = decoded.textVersions.headOption.map(_.`type`) shouldBe Some(Some("Submitted"))
    val _              = decoded.textVersions.headOption.map(_.formats.size) shouldBe Some(2)
    val _              = decoded.pagination.flatMap(_.count) shouldBe Some(2)
    decode[AmendmentTextResponseDTO](decoded.asJson.noSpaces) shouldBe Right(decoded)
  }

  it should "decode an empty textVersions list" in {
    val json           = """{"textVersions":[]}"""
    val Right(decoded) = decode[AmendmentTextResponseDTO](json): @unchecked
    val _              = decoded.textVersions shouldBe empty
    decoded.pagination shouldBe None
  }

}
