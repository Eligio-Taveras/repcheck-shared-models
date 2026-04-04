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
    amendedBill = Some(AmendedBillDTO(Some(118), Some(5678), Some("Senate"), Some("S"), Some("Test Bill"), Some("s"), None)),
    chamber = Some("Senate"),
    description = Some("Amendment description"),
    purpose = Some("To improve the bill"),
    sponsors = Some(List(SponsorDTO("S000033", Some("Bernard"), Some("Sanders"), None, None, None, Some("I"), Some("VT"), None))),
    submittedDate = Some("2024-02-15"),
    latestAction = Some(LatestActionDTO("2024-03-01", "Submitted")),
    updateDate = Some("2024-03-15")
  )

  "AmendmentDetailDTO.toDO" should "produce AmendmentDO with correct natural key" in {
    val Right(result) = validAmendmentDetail.toDO: @unchecked
    result.amendmentId shouldBe "118-SAMDT-200"
  }

  it should "map all fields correctly" in {
    val Right(a) = validAmendmentDetail.toDO: @unchecked
    a.congress shouldBe 118
    a.amendmentType shouldBe Some("SAMDT")
    a.number shouldBe "200"
    a.chamber shouldBe Some("Senate")
    a.description shouldBe Some("Amendment description")
    a.purpose shouldBe Some("To improve the bill")
    a.sponsorBioguideId shouldBe Some("S000033")
    a.submittedDate shouldBe Some("2024-02-15")
    a.latestActionDate shouldBe Some("2024-03-01")
    a.latestActionText shouldBe Some("Submitted")
    a.updateDate shouldBe Some("2024-03-15")
  }

  it should "construct billId from amendedBill" in {
    val Right(a) = validAmendmentDetail.toDO: @unchecked
    a.billId shouldBe Some("118-S-5678")
  }

  it should "use UNKNOWN when amendmentType is None" in {
    val dto = validAmendmentDetail.copy(amendmentType = None)
    val Right(a) = dto.toDO: @unchecked
    a.amendmentId shouldBe "118-UNKNOWN-200"
    a.amendmentType shouldBe None
  }

  it should "handle None amendedBill" in {
    val dto = validAmendmentDetail.copy(amendedBill = None)
    val Right(a) = dto.toDO: @unchecked
    a.billId shouldBe None
  }

  it should "handle None sponsors" in {
    val dto = validAmendmentDetail.copy(sponsors = None)
    val Right(a) = dto.toDO: @unchecked
    a.sponsorBioguideId shouldBe None
  }

  it should "handle None latestAction" in {
    val dto = validAmendmentDetail.copy(latestAction = None)
    val Right(a) = dto.toDO: @unchecked
    a.latestActionDate shouldBe None
    a.latestActionText shouldBe None
  }

  it should "fail when congress <= 0" in {
    val result = validAmendmentDetail.copy(congress = 0).toDO
    result.isLeft shouldBe true
    result.left.map(msg => msg.contains("congress")) shouldBe Left(true)
  }

  it should "fail when number is empty" in {
    val result = validAmendmentDetail.copy(number = "").toDO
    result.isLeft shouldBe true
    result.left.map(msg => msg.contains("number")) shouldBe Left(true)
  }

  it should "handle partial amendedBill (missing fields)" in {
    val dto = validAmendmentDetail.copy(
      amendedBill = Some(AmendedBillDTO(Some(118), None, None, None, None, Some("hr"), None))
    )
    val Right(a) = dto.toDO: @unchecked
    a.billId shouldBe None // number is None, so billId can't be constructed
  }

  "buildAmendmentId" should "construct correct natural key" in {
    AmendmentConversions.buildAmendmentId(118, Some("HAMDT"), "100") shouldBe "118-HAMDT-100"
    AmendmentConversions.buildAmendmentId(117, None, "50") shouldBe "117-UNKNOWN-50"
  }

  "buildBillIdFromAmendedBill" should "construct billId when all fields present" in {
    AmendmentConversions.buildBillIdFromAmendedBill(Some(118), Some("hr"), Some(1234)) shouldBe Some("118-HR-1234")
  }

  it should "return None when any field is missing" in {
    AmendmentConversions.buildBillIdFromAmendedBill(None, Some("hr"), Some(1234)) shouldBe None
    AmendmentConversions.buildBillIdFromAmendedBill(Some(118), None, Some(1234)) shouldBe None
    AmendmentConversions.buildBillIdFromAmendedBill(Some(118), Some("hr"), None) shouldBe None
  }
}
