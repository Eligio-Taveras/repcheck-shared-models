package repcheck.shared.models.congress.dto.amendment

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.bill.{LatestActionDTO, SponsorDTO}
import repcheck.shared.models.congress.dto.common.{PaginationInfoDTO, ResourceLinkDTO}

class AmendmentDTOsSpec extends AnyFlatSpec with Matchers {

  "AmendedBillDTO" should "round-trip" in {
    val dto = AmendedBillDTO(
      congress = Some(118),
      number = Some("1234"),
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

  it should "decode JSON `type` field into billType (real API field name)" in {
    val json   = """{"congress":117,"number":"3684","type":"hr","title":"Infrastructure Investment and Jobs Act"}"""
    val result = decode[AmendedBillDTO](json)
    val _      = result.map(_.billType) shouldBe Right(Some("hr"))
    val _      = result.map(_.number) shouldBe Right(Some("3684"))
    result.map(_.congress) shouldBe Right(Some(117))
  }

  it should "decode `billType` field as fallback when `type` absent (backward compat)" in {
    val json = """{"billType":"hr","number":"3684"}"""
    decode[AmendedBillDTO](json).map(_.billType) shouldBe Right(Some("hr"))
  }

  "AmendedAmendmentDTO" should "round-trip" in {
    val dto = AmendedAmendmentDTO(
      congress = Some(117),
      number = Some("2137"),
      amendmentType = Some("SAMDT"),
      purpose = Some("In the nature of a substitute."),
      updateDate = Some("2022-02-08T17:27:59Z"),
      url = Some("https://api.congress.gov/v3/amendment/117/samdt/2137"),
    )
    decode[AmendedAmendmentDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with all None fields" in {
    decode[AmendedAmendmentDTO]("{}") shouldBe Right(AmendedAmendmentDTO(None, None, None, None, None, None))
  }

  it should "decode JSON `type` field into amendmentType (real API field name)" in {
    val json =
      """{"congress":117,"number":"2137","type":"SAMDT","purpose":"In the nature of a substitute.","updateDate":"2022-02-08T17:27:59Z","url":"https://x"}"""
    val result = decode[AmendedAmendmentDTO](json)
    val _      = result.map(_.amendmentType) shouldBe Right(Some("SAMDT"))
    val _      = result.map(_.updateDate) shouldBe Right(Some("2022-02-08T17:27:59Z"))
    result.map(_.purpose) shouldBe Right(Some("In the nature of a substitute."))
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

  it should "decode JSON `type` field into amendmentType" in {
    val json   = """{"congress":117,"number":"2137","type":"SAMDT"}"""
    val result = decode[AmendmentListItemDTO](json)
    result.map(_.amendmentType) shouldBe Right(Some("SAMDT"))
  }

  "AmendmentDetailDTO" should "round-trip with all fields" in {
    val dto = AmendmentDetailDTO(
      congress = 117,
      number = "2137",
      amendmentType = Some("SAMDT"),
      amendedBill = Some(
        AmendedBillDTO(
          Some(117),
          Some("3684"),
          Some("Senate"),
          Some("S"),
          Some("Infrastructure Investment and Jobs Act"),
          Some("hr"),
          Some("https://api.congress.gov/v3/bill/117/hr/3684"),
          Some("2022-09-29T03:27:31Z"),
        )
      ),
      amendedAmendment = Some(
        AmendedAmendmentDTO(
          Some(117),
          Some("2137"),
          Some("SAMDT"),
          Some("In the nature of a substitute."),
          Some("2022-02-08T17:27:59Z"),
          Some("https://api.congress.gov/v3/amendment/117/samdt/2137"),
        )
      ),
      chamber = Some("Senate"),
      description = Some("Amendment description"),
      purpose = Some("To improve the bill"),
      sponsors = Some(
        List(SponsorDTO("S000033", Some("Bernard"), Some("Sanders"), None, None, None, Some("I"), Some("VT"), None))
      ),
      submittedDate = Some("2021-08-01T04:00:00Z"),
      proposedDate = Some("2021-08-01T04:00:00Z"),
      latestAction =
        Some(LatestActionDTO(actionDate = "2024-03-01", text = "Submitted", actionTime = Some("14:30:00"))),
      updateDate = Some("2024-03-15"),
      actions = Some(ResourceLinkDTO(Some(18), Some("https://api.congress.gov/v3/amendment/117/samdt/2137/actions"))),
      textVersions = Some(ResourceLinkDTO(Some(1), Some("https://api.congress.gov/v3/amendment/117/samdt/2137/text"))),
      cosponsors =
        Some(ResourceLinkDTO(Some(0), Some("https://api.congress.gov/v3/amendment/117/samdt/2137/cosponsors"))),
      amendmentsToAmendment = Some(
        ResourceLinkDTO(Some(2), Some("https://api.congress.gov/v3/amendment/117/samdt/2137/amendments"))
      ),
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
    val _      = result.map(_.actions) shouldBe Right(None)
    val _      = result.map(_.textVersions) shouldBe Right(None)
    val _      = result.map(_.cosponsors) shouldBe Right(None)
    val _      = result.map(_.amendmentsToAmendment) shouldBe Right(None)
    result.map(_.proposedDate) shouldBe Right(None)
  }

  it should "decode JSON `type` field into amendmentType" in {
    val json   = """{"congress":117,"number":"2137","type":"SAMDT"}"""
    val result = decode[AmendmentDetailDTO](json)
    result.map(_.amendmentType) shouldBe Right(Some("SAMDT"))
  }

  it should "decode realistic Congress.gov SAMDT 2137 detail-shape fixture (live-API verified)" in {
    // Constructed from the verified shape of /v3/amendment/117/samdt/2137. Exercises every silent-None bug we hit:
    //   - JSON `type` -> Scala `amendmentType`
    //   - `actions` and `textVersions` as `{count, url}` (not arrays)
    //   - `proposedDate` populated
    //   - `amendedBill.number` as a string
    //   - `amendedAmendment.type` -> `amendmentType`
    val fixture =
      """{
        |  "congress": 117,
        |  "number": "2137",
        |  "type": "SAMDT",
        |  "amendedBill": {
        |    "congress": 117,
        |    "number": "3684",
        |    "originChamber": "House",
        |    "originChamberCode": "H",
        |    "title": "Infrastructure Investment and Jobs Act",
        |    "type": "HR",
        |    "url": "https://api.congress.gov/v3/bill/117/hr/3684?format=json",
        |    "updateDateIncludingText": "2022-09-29T03:27:31Z"
        |  },
        |  "amendedAmendment": {
        |    "congress": 117,
        |    "number": "2137",
        |    "type": "SAMDT",
        |    "purpose": "In the nature of a substitute.",
        |    "updateDate": "2022-02-08T17:27:59Z",
        |    "url": "https://api.congress.gov/v3/amendment/117/samdt/2137?format=json"
        |  },
        |  "chamber": "Senate",
        |  "description": "Amendment description",
        |  "purpose": "To strike sections.",
        |  "submittedDate": "2021-08-01T04:00:00Z",
        |  "proposedDate": "2021-08-01T04:00:00Z",
        |  "latestAction": {"actionDate": "2021-08-10", "text": "Amendment SA 2137 agreed to in Senate by Yea-Nay Vote."},
        |  "updateDate": "2022-02-08T17:27:59Z",
        |  "actions":               {"count": 18, "url": "https://api.congress.gov/v3/amendment/117/samdt/2137/actions?format=json"},
        |  "textVersions":          {"count": 1,  "url": "https://api.congress.gov/v3/amendment/117/samdt/2137/text?format=json"},
        |  "cosponsors":            {"count": 0,  "url": "https://api.congress.gov/v3/amendment/117/samdt/2137/cosponsors?format=json"},
        |  "amendmentsToAmendment": {"count": 2,  "url": "https://api.congress.gov/v3/amendment/117/samdt/2137/amendments?format=json"}
        |}""".stripMargin

    val Right(decoded) = decode[AmendmentDetailDTO](fixture): @unchecked
    val _              = decoded.amendmentType shouldBe Some("SAMDT")
    val _              = decoded.proposedDate shouldBe Some("2021-08-01T04:00:00Z")
    val _              = decoded.actions.flatMap(_.count) shouldBe Some(18)
    val _              = decoded.actions.flatMap(_.url).getOrElse("") should include("/actions")
    val _              = decoded.textVersions.flatMap(_.count) shouldBe Some(1)
    val _              = decoded.textVersions.flatMap(_.url).getOrElse("") should include("/text")
    val _              = decoded.cosponsors.flatMap(_.count) shouldBe Some(0)
    val _              = decoded.amendmentsToAmendment.flatMap(_.count) shouldBe Some(2)
    val _              = decoded.amendedBill.flatMap(_.number) shouldBe Some("3684")
    val _              = decoded.amendedBill.flatMap(_.billType) shouldBe Some("HR")
    val _              = decoded.amendedAmendment.flatMap(_.amendmentType) shouldBe Some("SAMDT")
    val _              = decoded.amendedAmendment.flatMap(_.updateDate) shouldBe Some("2022-02-08T17:27:59Z")
    decoded.amendedAmendment.flatMap(_.purpose) shouldBe Some("In the nature of a substitute.")
  }

  it should "fall back to None for actions/textVersions when API ever inlines them as arrays" in {
    val json =
      """{"congress":117,"number":"2137","actions":[{"actionDate":"2021-08-01","text":"Submitted"}],"textVersions":["v1"]}"""
    val Right(decoded) = decode[AmendmentDetailDTO](json): @unchecked
    val _              = decoded.actions shouldBe None
    decoded.textVersions shouldBe None
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
