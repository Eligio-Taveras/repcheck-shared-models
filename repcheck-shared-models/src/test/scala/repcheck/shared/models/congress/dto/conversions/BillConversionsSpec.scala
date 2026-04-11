package repcheck.shared.models.congress.dto.conversions

import java.time.{Instant, LocalDate}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.bill.TextVersionCode
import repcheck.shared.models.congress.common.{BillType, Chamber, FormatType}
import repcheck.shared.models.congress.dto.bill._
import repcheck.shared.models.congress.dto.common.PaginationInfoDTO
import repcheck.shared.models.congress.dto.conversions.BillConversions._

class BillConversionsSpec extends AnyFlatSpec with Matchers {

  private val validBillListItem = BillListItemDTO(
    congress = 118,
    number = "1234",
    billType = "hr",
    latestAction = Some(LatestActionDTO("2024-01-15", "Referred to committee")),
    originChamber = Some("House"),
    originChamberCode = Some("H"),
    title = "A bill to do something",
    updateDate = Some("2024-02-01"),
    updateDateIncludingText = Some("2024-02-15"),
    url = "https://api.congress.gov/v3/bill/118/hr/1234",
  )

  "BillListItemDTO.toDO" should "convert with correct natural key" in {
    val result = validBillListItem.toDO
    val _      = result.isRight shouldBe true
    val _      = result.map(_.billId) shouldBe Right(0L)
    result.map(_.naturalKey) shouldBe Right("118-HR-1234")
  }

  it should "map all list-level fields correctly" in {
    val Right(bill) = validBillListItem.toDO: @unchecked
    val _           = bill.congress shouldBe 118
    val _           = bill.billType shouldBe BillType.HR
    val _           = bill.number shouldBe "1234"
    val _           = bill.title shouldBe "A bill to do something"
    val _           = bill.originChamber shouldBe Some(Chamber.House)
    val _           = bill.originChamberCode shouldBe Some("H")
    val _           = bill.latestActionDate shouldBe Some(LocalDate.parse("2024-01-15"))
    val _           = bill.latestActionText shouldBe Some("Referred to committee")
    val _           = bill.updateDate shouldBe Some(Instant.parse("2024-02-01T00:00:00Z"))
    val _           = bill.updateDateIncludingText shouldBe Some(Instant.parse("2024-02-15T00:00:00Z"))
    bill.apiUrl shouldBe Some("https://api.congress.gov/v3/bill/118/hr/1234")
  }

  it should "set detail-only fields to None" in {
    val Right(bill) = validBillListItem.toDO: @unchecked
    val _           = bill.introducedDate shouldBe None
    val _           = bill.policyArea shouldBe None
    val _           = bill.sponsorMemberId shouldBe None
    val _           = bill.textUrl shouldBe None
    val _           = bill.summaryText shouldBe None
    bill.legislationUrl shouldBe None
  }

  it should "fail when congress <= 0" in {
    val result = validBillListItem.copy(congress = 0).toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("congress")) shouldBe Left(true)
  }

  it should "fail when number is empty" in {
    val result = validBillListItem.copy(number = "").toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("number")) shouldBe Left(true)
  }

  it should "fail when title is empty" in {
    val result = validBillListItem.copy(title = "  ").toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("title")) shouldBe Left(true)
  }

  private val validBillDetail = BillDetailDTO(
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
    actions = None,
    committees = None,
    sponsors =
      Some(List(SponsorDTO("S000033", Some("Bernie"), Some("Sanders"), None, None, None, Some("I"), Some("VT"), None))),
    cosponsors = Some(PaginationInfoDTO(Some(25), None)),
    subjects = Some(
      BillSubjectsDTO(
        Some(
          List(
            LegislativeSubjectDTO("Roads and highways", Some("2024-01-15")),
            LegislativeSubjectDTO("Infrastructure development", None),
          )
        ),
        Some("Transportation"),
      )
    ),
    summaries = Some(
      List(BillSummaryDTO(Some("2024-01-10"), Some("Introduced in Senate"), Some("Summary text"), None, Some("00")))
    ),
    textVersions = Some(
      List(
        TextVersionDTO(
          Some("2024-01-10"),
          Some(List(FormatDTO("Formatted Text", "https://example.com/text"))),
          Some("Introduced in Senate"),
        )
      )
    ),
    titles = None,
    constitutionalAuthorityStatementText = Some("Congress has the power..."),
    cboCostEstimates = None,
    committeeReports = None,
    relatedBills = None,
    legislationUrl = Some("https://congress.gov/bill/118/s/5678"),
  )

  "BillDetailDTO.toDO" should "produce BillConversionResult with correct BillDO" in {
    val Right(result) = validBillDetail.toDO: @unchecked
    val _             = result.bill.billId shouldBe 0L
    val _             = result.bill.naturalKey shouldBe "118-S-5678"
    val _             = result.bill.sponsorMemberId shouldBe None
    val _             = result.bill.introducedDate shouldBe Some(LocalDate.parse("2024-01-10"))
    val _             = result.bill.policyArea shouldBe Some("Transportation")
    val _             = result.bill.constitutionalAuthorityText shouldBe Some("Congress has the power...")
    result.bill.legislationUrl shouldBe Some("https://congress.gov/bill/118/s/5678")
  }

  it should "extract text info from first textVersion" in {
    val Right(result) = validBillDetail.toDO: @unchecked
    val _             = result.bill.textUrl shouldBe Some("https://example.com/text")
    val _             = result.bill.textFormat shouldBe Some(FormatType.FormattedText)
    val _             = result.bill.textVersionType shouldBe Some(TextVersionCode.IS)
    result.bill.textDate shouldBe Some(LocalDate.parse("2024-01-10"))
  }

  it should "extract summary from first summary" in {
    val Right(result) = validBillDetail.toDO: @unchecked
    val _             = result.bill.summaryText shouldBe Some("Summary text")
    val _             = result.bill.summaryActionDesc shouldBe Some("Introduced in Senate")
    result.bill.summaryActionDate shouldBe Some(LocalDate.parse("2024-01-10"))
  }

  it should "produce subjects list from legislativeSubjects" in {
    val Right(result) = validBillDetail.toDO: @unchecked
    val _             = result.subjects.length shouldBe 2
    val _ = result.subjects.map(_.subjectName) shouldBe List("Roads and highways", "Infrastructure development")
    val _ = result.subjects.map(_.billId).distinct shouldBe List(0L)
    result.subjects.headOption.flatMap(_.updateDate) shouldBe Some(Instant.parse("2024-01-15T00:00:00Z"))
  }

  it should "produce empty cosponsors list (cosponsors are not in BillDetailDTO)" in {
    val Right(result) = validBillDetail.toDO: @unchecked
    result.cosponsors shouldBe List.empty
  }

  it should "fail when congress <= 0" in {
    val result = validBillDetail.copy(congress = -1).toDO
    result.isLeft shouldBe true
  }

  it should "fail when number is empty" in {
    val result = validBillDetail.copy(number = "").toDO
    result.isLeft shouldBe true
  }

  it should "handle None text versions" in {
    val Right(result) = validBillDetail.copy(textVersions = None).toDO: @unchecked
    val _             = result.bill.textUrl shouldBe None
    result.bill.textFormat shouldBe None
  }

  it should "handle None subjects" in {
    val Right(result) = validBillDetail.copy(subjects = None).toDO: @unchecked
    result.subjects shouldBe List.empty
  }

  "buildBillId" should "construct correct natural key" in {
    val _ = BillConversions.buildBillId(118, "hr", "1234") shouldBe "118-HR-1234"
    BillConversions.buildBillId(117, "sjres", "10") shouldBe "117-SJRES-10"
  }

}
