package repcheck.shared.models.congress.dto.conversions

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.amendment._
import repcheck.shared.models.congress.dto.bill.{LatestActionDTO, SponsorDTO}
import repcheck.shared.models.congress.dto.conversions.AmendmentConversions._

class AmendmentConversionsSpec extends AnyFlatSpec with Matchers {

  private val validAmendmentDetail = AmendmentDetailDTO(
    congress = 118,
    number = "200",
    amendmentType = Some("SAMDT"),
    amendedBill =
      Some(AmendedBillDTO(Some(118), Some(5678), Some("Senate"), Some("S"), Some("Test Bill"), Some("s"), None, None)),
    chamber = Some("Senate"),
    description = Some("Amendment description"),
    purpose = Some("To improve the bill"),
    sponsors = Some(
      List(SponsorDTO("S000033", Some("Bernard"), Some("Sanders"), None, None, None, Some("I"), Some("VT"), None))
    ),
    submittedDate = Some("2024-02-15"),
    latestAction = Some(LatestActionDTO("2024-03-01", "Submitted")),
    updateDate = Some("2024-03-15"),
    actions = None,
    textVersions = None,
  )

  "AmendmentDetailDTO.toDO" should "produce AmendmentDO with correct natural key" in {
    val Right(result) = validAmendmentDetail.toDO: @unchecked
    val _             = result.amendmentId shouldBe 0L
    result.naturalKey shouldBe "118-SAMDT-200"
  }

  it should "map all fields correctly" in {
    val Right(a) = validAmendmentDetail.toDO: @unchecked
    val _        = a.congress shouldBe 118
    val _        = a.amendmentType shouldBe Some("SAMDT")
    val _        = a.number shouldBe "200"
    val _        = a.chamber shouldBe Some("Senate")
    val _        = a.description shouldBe Some("Amendment description")
    val _        = a.purpose shouldBe Some("To improve the bill")
    val _        = a.sponsorMemberId shouldBe None
    val _        = a.submittedDate shouldBe Some("2024-02-15")
    val _        = a.latestActionDate shouldBe Some("2024-03-01")
    val _        = a.latestActionText shouldBe Some("Submitted")
    a.updateDate shouldBe Some("2024-03-15")
  }

  it should "set billId to None (FK resolved at persistence time)" in {
    val Right(a) = validAmendmentDetail.toDO: @unchecked
    a.billId shouldBe None
  }

  it should "use UNKNOWN when amendmentType is None" in {
    val dto      = validAmendmentDetail.copy(amendmentType = None)
    val Right(a) = dto.toDO: @unchecked
    val _        = a.amendmentId shouldBe 0L
    val _        = a.naturalKey shouldBe "118-UNKNOWN-200"
    a.amendmentType shouldBe None
  }

  it should "handle None amendedBill" in {
    val dto      = validAmendmentDetail.copy(amendedBill = None)
    val Right(a) = dto.toDO: @unchecked
    a.billId shouldBe None
  }

  it should "handle None sponsors" in {
    val dto      = validAmendmentDetail.copy(sponsors = None)
    val Right(a) = dto.toDO: @unchecked
    a.sponsorMemberId shouldBe None
  }

  it should "handle None latestAction" in {
    val dto      = validAmendmentDetail.copy(latestAction = None)
    val Right(a) = dto.toDO: @unchecked
    val _        = a.latestActionDate shouldBe None
    a.latestActionText shouldBe None
  }

  it should "fail when congress <= 0" in {
    val result = validAmendmentDetail.copy(congress = 0).toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("congress")) shouldBe Left(true)
  }

  it should "fail when number is empty" in {
    val result = validAmendmentDetail.copy(number = "").toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("number")) shouldBe Left(true)
  }

  it should "handle partial amendedBill (missing fields)" in {
    val dto = validAmendmentDetail.copy(
      amendedBill = Some(AmendedBillDTO(Some(118), None, None, None, None, Some("hr"), None, None))
    )
    val Right(a) = dto.toDO: @unchecked
    a.billId shouldBe None // number is None, so billId can't be constructed
  }

  "buildAmendmentId" should "construct correct natural key" in {
    val _ = AmendmentConversions.buildAmendmentId(118, Some("HAMDT"), "100") shouldBe "118-HAMDT-100"
    AmendmentConversions.buildAmendmentId(117, None, "50") shouldBe "117-UNKNOWN-50"
  }

  "buildBillIdFromAmendedBill" should "construct billId when all fields present" in {
    AmendmentConversions.buildBillIdFromAmendedBill(Some(118), Some("hr"), Some(1234)) shouldBe Some("118-HR-1234")
  }

  it should "return None when any field is missing" in {
    val _ = AmendmentConversions.buildBillIdFromAmendedBill(None, Some("hr"), Some(1234)) shouldBe None
    val _ = AmendmentConversions.buildBillIdFromAmendedBill(Some(118), None, Some(1234)) shouldBe None
    AmendmentConversions.buildBillIdFromAmendedBill(Some(118), Some("hr"), None) shouldBe None
  }

}
