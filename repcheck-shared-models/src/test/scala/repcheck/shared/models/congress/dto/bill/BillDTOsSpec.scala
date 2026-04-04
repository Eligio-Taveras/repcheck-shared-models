package repcheck.shared.models.congress.dto.bill

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.common.PaginationInfoDTO

class BillDTOsSpec extends AnyFlatSpec with Matchers {

  "LatestActionDTO" should "round-trip" in {
    val dto = LatestActionDTO(actionDate = "2024-01-15", text = "Passed House")
    decode[LatestActionDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "SourceSystemDTO" should "round-trip" in {
    val dto = SourceSystemDTO(code = Some(9), name = Some("Library of Congress"))
    decode[SourceSystemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with missing optional fields" in {
    decode[SourceSystemDTO]("{}") shouldBe Right(SourceSystemDTO(None, None))
  }

  "SponsorDTO" should "round-trip with all fields" in {
    val dto = SponsorDTO(
      bioguideId = "B001297",
      firstName = Some("Ken"),
      lastName = Some("Buck"),
      fullName = Some("Rep. Buck, Ken [R-CO-4]"),
      middleName = Some("Robert"),
      isByRequest = Some("N"),
      party = Some("R"),
      state = Some("CO"),
      url = Some("https://api.congress.gov/v3/member/B001297")
    )
    decode[SponsorDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "CoSponsorDTO" should "round-trip with all fields" in {
    val dto = CoSponsorDTO(
      bioguideId = "A000370",
      district = Some(12),
      firstName = Some("Alma"),
      fullName = Some("Rep. Adams, Alma S. [D-NC-12]"),
      isOriginalCosponsor = Some(true),
      lastName = Some("Adams"),
      party = Some("D"),
      sponsorshipDate = Some("2024-02-01"),
      state = Some("NC"),
      url = Some("https://api.congress.gov/v3/member/A000370")
    )
    decode[CoSponsorDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "BillActionDTO" should "encode 'type' field correctly and round-trip" in {
    val dto = BillActionDTO(
      actionCode = Some("H11100"),
      actionDate = "2024-01-15",
      sourceSystem = Some(SourceSystemDTO(Some(2), Some("House floor actions"))),
      text = "Referred to the Committee on Judiciary.",
      actionType = Some("IntroReferral")
    )
    val json = dto.asJson
    json.hcursor.downField("type").as[String] shouldBe Right("IntroReferral")
    json.as[BillActionDTO] shouldBe Right(dto)
  }

  it should "decode JSON with 'type' field mapped to actionType" in {
    val json = """{"actionDate":"2024-01-15","text":"Test","type":"Floor"}"""
    val result = decode[BillActionDTO](json)
    result.map(_.actionType) shouldBe Right(Some("Floor"))
  }

  "LegislativeSubjectDTO" should "round-trip" in {
    val dto = LegislativeSubjectDTO(name = "Armed forces and national security", updateDate = Some("2024-03-01"))
    decode[LegislativeSubjectDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "BillSubjectsDTO" should "round-trip" in {
    val dto = BillSubjectsDTO(
      legislativeSubjects = Some(List(LegislativeSubjectDTO("Health", None))),
      policyArea = Some("Health")
    )
    decode[BillSubjectsDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "BillSummaryDTO" should "round-trip" in {
    val dto = BillSummaryDTO(
      actionDate = Some("2024-01-15"),
      actionDesc = Some("Introduced in House"),
      text = Some("<p>This bill does something.</p>"),
      updateDate = Some("2024-02-01"),
      versionCode = Some("00")
    )
    decode[BillSummaryDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "RelationshipDetailDTO" should "encode 'type' field and round-trip" in {
    val dto = RelationshipDetailDTO(identifiedBy = Some("CRS"), relationshipType = Some("Related bill"))
    val json = dto.asJson
    json.hcursor.downField("type").as[Option[String]] shouldBe Right(Some("Related bill"))
    json.as[RelationshipDetailDTO] shouldBe Right(dto)
  }

  "RelatedBillDTO" should "round-trip" in {
    val dto = RelatedBillDTO(
      congress = Some(118),
      number = Some(1234),
      latestAction = Some(LatestActionDTO("2024-01-01", "Introduced")),
      relationshipDetails = Some(List(RelationshipDetailDTO(Some("CRS"), Some("Identical bill"))))
    )
    decode[RelatedBillDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "CboCostEstimateDTO" should "round-trip" in {
    val dto = CboCostEstimateDTO(
      description = Some("As ordered reported"),
      pubDate = Some("2024-03-15"),
      title = Some("HR 1234"),
      url = Some("https://www.cbo.gov/publication/12345")
    )
    decode[CboCostEstimateDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "CommitteeReportDTO" should "round-trip" in {
    val dto = CommitteeReportDTO(citation = Some("H. Rept. 118-100"), url = Some("https://example.com"))
    decode[CommitteeReportDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "TitleDTO" should "round-trip" in {
    val dto = TitleDTO(
      title = "National Defense Authorization Act",
      updateDate = Some("2024-01-01"),
      titleType = Some("Official Title as Introduced"),
      titleTypeCode = Some(1),
      billTextVersionCode = Some("IH"),
      billTextVersionName = Some("Introduced in House")
    )
    decode[TitleDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "BillListItemDTO" should "round-trip with all fields" in {
    val dto = BillListItemDTO(
      congress = 118,
      number = "1234",
      billType = "hr",
      latestAction = Some(LatestActionDTO("2024-01-15", "Referred to committee")),
      originChamber = Some("House"),
      originChamberCode = Some("H"),
      title = "A bill to do something",
      updateDate = Some("2024-02-01T00:00:00Z"),
      updateDateIncludingText = Some("2024-02-15T00:00:00Z"),
      url = "https://api.congress.gov/v3/bill/118/hr/1234"
    )
    decode[BillListItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with missing optional fields" in {
    val json = """{
      "congress": 118,
      "number": "1234",
      "billType": "hr",
      "title": "Test bill",
      "url": "https://example.com"
    }"""
    val result = decode[BillListItemDTO](json)
    result.isRight shouldBe true
    result.map(_.latestAction) shouldBe Right(None)
    result.map(_.originChamber) shouldBe Right(None)
  }

  "BillDetailDTO" should "round-trip with all fields" in {
    val dto = BillDetailDTO(
      congress = 118,
      number = "5678",
      billType = "s",
      latestAction = Some(LatestActionDTO("2024-06-01", "Passed Senate")),
      originChamber = Some("Senate"),
      originChamberCode = Some("S"),
      title = "Infrastructure Investment Act",
      updateDate = Some("2024-06-15"),
      updateDateIncludingText = Some("2024-06-20"),
      url = "https://api.congress.gov/v3/bill/118/s/5678",
      introducedDate = Some("2024-01-10"),
      policyArea = Some("Transportation"),
      sponsors = Some(List(SponsorDTO("S000033", Some("Bernie"), Some("Sanders"), Some("Sen. Sanders"), None, None, Some("I"), Some("VT"), None))),
      cosponsors = Some(PaginationInfoDTO(Some(25), Some("https://api.congress.gov/v3/bill/118/s/5678/cosponsors"))),
      subjects = Some(BillSubjectsDTO(Some(List(LegislativeSubjectDTO("Roads and highways", None))), Some("Transportation"))),
      summaries = Some(List(BillSummaryDTO(Some("2024-01-10"), Some("Introduced in Senate"), Some("Summary text"), None, Some("00")))),
      textVersions = Some(List(TextVersionDTO(Some("2024-01-10"), Some(List(FormatDTO("Formatted Text", "https://example.com/text"))), Some("Introduced in Senate")))),
      titles = Some(List(TitleDTO("Infrastructure Investment Act", None, Some("Short Title(s) as Introduced"), Some(1), None, None))),
      constitutionalAuthorityStatementText = Some("Congress has the power..."),
      cboCostEstimates = Some(List(CboCostEstimateDTO(Some("As ordered"), None, Some("S. 5678"), Some("https://cbo.gov")))),
      committeeReports = Some(List(CommitteeReportDTO(Some("S. Rept. 118-50"), Some("https://example.com/report")))),
      relatedBills = Some(List(RelatedBillDTO(Some(118), Some(9999), None, None))),
      legislationUrl = Some("https://congress.gov/bill/118th-congress/senate-bill/5678")
    )
    decode[BillDetailDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with only required fields" in {
    val json = """{
      "congress": 118,
      "number": "100",
      "billType": "hr",
      "title": "Minimal bill",
      "url": "https://example.com"
    }"""
    val result = decode[BillDetailDTO](json)
    result.isRight shouldBe true
    result.map(_.sponsors) shouldBe Right(None)
    result.map(_.summaries) shouldBe Right(None)
    result.map(_.textVersions) shouldBe Right(None)
  }
}
