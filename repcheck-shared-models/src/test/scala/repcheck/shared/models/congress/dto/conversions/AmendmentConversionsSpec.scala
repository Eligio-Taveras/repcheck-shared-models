package repcheck.shared.models.congress.dto.conversions

import java.time.{Instant, LocalDate}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.amendment.{AmendmentType, SponsorType}
import repcheck.shared.models.congress.common.Chamber
import repcheck.shared.models.congress.dto.amendment._
import repcheck.shared.models.congress.dto.bill.{LatestActionDTO, SponsorDTO}
import repcheck.shared.models.congress.dto.conversions.AmendmentConversions._

class AmendmentConversionsSpec extends AnyFlatSpec with Matchers {

  private val validAmendmentDetail = AmendmentDetailDTO(
    congress = 118,
    number = "200",
    amendmentType = Some("SAMDT"),
    amendedBill = Some(
      AmendedBillDTO(
        Some(118),
        Some("5678"),
        Some("Senate"),
        Some("S"),
        Some("Test Bill"),
        Some("s"),
        None,
        None,
      )
    ),
    amendedAmendment = None,
    chamber = Some("Senate"),
    description = Some("Amendment description"),
    purpose = Some("To improve the bill"),
    sponsors = Some(
      List(
        SponsorDTO.MemberSponsorDTO(
          "S000033",
          Some("Bernard"),
          Some("Sanders"),
          None,
          None,
          None,
          Some("I"),
          Some("VT"),
          None,
          None,
        )
      )
    ),
    submittedDate = Some("2024-02-15"),
    proposedDate = Some("2024-02-16"),
    latestAction = Some(LatestActionDTO(actionDate = "2024-03-01", text = "Submitted", actionTime = Some("14:30:00"))),
    updateDate = Some("2024-03-15"),
    actions = None,
    textVersions = None,
    cosponsors = None,
    amendmentsToAmendment = None,
  )

  "AmendmentDetailDTO.toDO()" should "produce AmendmentDO with correct natural key" in {
    val Right(result) = validAmendmentDetail.toDO(): @unchecked
    val _             = result.amendmentId shouldBe 0L
    result.naturalKey shouldBe "118-SAMDT-200"
  }

  it should "map all fields correctly including latestActionTime, proposedDate, and derived sponsorType" in {
    val Right(a) = validAmendmentDetail.toDO(): @unchecked
    val _        = a.congress shouldBe 118
    val _        = a.amendmentType shouldBe Some(AmendmentType.SAMDT)
    val _        = a.number shouldBe "200"
    val _        = a.chamber shouldBe Chamber.Senate
    val _        = a.description shouldBe Some("Amendment description")
    val _        = a.purpose shouldBe Some("To improve the bill")
    val _        = a.sponsorMemberId shouldBe None
    val _        = a.sponsorType shouldBe Some(SponsorType.Member)
    val _        = a.submittedDate shouldBe Some(LocalDate.parse("2024-02-15"))
    val _        = a.proposedDate shouldBe Some(LocalDate.parse("2024-02-16"))
    val _        = a.latestActionDate shouldBe Some(LocalDate.parse("2024-03-01"))
    val _        = a.latestActionTime shouldBe Some("14:30:00")
    val _        = a.latestActionText shouldBe Some("Submitted")
    val _        = a.updateDate shouldBe Some(Instant.parse("2024-03-15T00:00:00Z"))
    val _        = a.parentAmendmentId shouldBe None
    a.lastTextCheckAt shouldBe None
  }

  it should "derive SponsorType.Member from MemberSponsorDTO in sponsors list" in {
    val Right(a) = validAmendmentDetail.toDO(): @unchecked
    a.sponsorType shouldBe Some(SponsorType.Member)
  }

  it should "derive SponsorType.Committee from CommitteeSponsorDTO in sponsors list" in {
    val committeeSponsored = validAmendmentDetail.copy(
      sponsors = Some(List(SponsorDTO.CommitteeSponsorDTO("Committee on Finance", "https://example.com")))
    )
    val Right(a) = committeeSponsored.toDO(): @unchecked
    a.sponsorType shouldBe Some(SponsorType.Committee)
  }

  it should "leave sponsorType as None when sponsors list is empty" in {
    val Right(a) = validAmendmentDetail.copy(sponsors = Some(Nil)).toDO(): @unchecked
    a.sponsorType shouldBe None
  }

  it should "leave sponsorType as None when sponsors is None" in {
    val Right(a) = validAmendmentDetail.copy(sponsors = None).toDO(): @unchecked
    a.sponsorType shouldBe None
  }

  it should "parse proposedDate from ISO datetime with offset (real-API shape)" in {
    val dto      = validAmendmentDetail.copy(proposedDate = Some("2021-08-01T04:00:00Z"))
    val Right(a) = dto.toDO(): @unchecked
    a.proposedDate shouldBe Some(LocalDate.parse("2021-08-01"))
  }

  it should "leave proposedDate as None when DTO has no proposedDate" in {
    val Right(a) = validAmendmentDetail.copy(proposedDate = None).toDO(): @unchecked
    a.proposedDate shouldBe None
  }

  it should "leave latestActionTime as None when latestAction has no actionTime" in {
    val Right(a) =
      validAmendmentDetail.copy(latestAction = Some(LatestActionDTO("2024-03-01", "Submitted"))).toDO(): @unchecked
    a.latestActionTime shouldBe None
  }

  it should "default billId / sponsorMemberId / sponsorCommitteeId / parentAmendmentId to None" in {
    val Right(a) = validAmendmentDetail.toDO(): @unchecked
    val _        = a.billId shouldBe None
    val _        = a.sponsorMemberId shouldBe None
    val _        = a.sponsorCommitteeId shouldBe None
    a.parentAmendmentId shouldBe None
  }

  it should "use UNKNOWN in natural key when amendmentType is None and an explicit chamber is provided" in {
    val dto      = validAmendmentDetail.copy(amendmentType = None)
    val Right(a) = dto.toDO(): @unchecked
    val _        = a.amendmentId shouldBe 0L
    val _        = a.naturalKey shouldBe "118-UNKNOWN-200"
    val _        = a.amendmentType shouldBe None
    a.chamber shouldBe Chamber.Senate
  }

  it should "handle None amendedBill" in {
    val dto      = validAmendmentDetail.copy(amendedBill = None)
    val Right(a) = dto.toDO(): @unchecked
    a.billId shouldBe None
  }

  it should "handle None sponsors" in {
    val dto      = validAmendmentDetail.copy(sponsors = None)
    val Right(a) = dto.toDO(): @unchecked
    a.sponsorMemberId shouldBe None
  }

  it should "handle None latestAction" in {
    val dto      = validAmendmentDetail.copy(latestAction = None)
    val Right(a) = dto.toDO(): @unchecked
    val _        = a.latestActionDate shouldBe None
    val _        = a.latestActionTime shouldBe None
    a.latestActionText shouldBe None
  }

  it should "fail when congress <= 0" in {
    val result = validAmendmentDetail.copy(congress = 0).toDO()
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("congress")) shouldBe Left(true)
  }

  it should "fail when number is empty" in {
    val result = validAmendmentDetail.copy(number = "").toDO()
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("number")) shouldBe Left(true)
  }

  it should "fail when chamber is unrecognized" in {
    val result = validAmendmentDetail.copy(chamber = Some("InvalidChamber")).toDO()
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("InvalidChamber")) shouldBe Left(true)
  }

  it should "fail when amendmentType is unrecognized" in {
    val result = validAmendmentDetail.copy(amendmentType = Some("BADTYPE")).toDO()
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("BADTYPE")) shouldBe Left(true)
  }

  it should "derive Senate chamber from SAMDT when DTO chamber is missing" in {
    val dto      = validAmendmentDetail.copy(chamber = None)
    val Right(a) = dto.toDO(): @unchecked
    a.chamber shouldBe Chamber.Senate
  }

  it should "derive House chamber from HAMDT when DTO chamber is missing" in {
    val dto      = validAmendmentDetail.copy(chamber = None, amendmentType = Some("HAMDT"))
    val Right(a) = dto.toDO(): @unchecked
    a.chamber shouldBe Chamber.House
  }

  it should "derive Senate chamber from SUAMDT when DTO chamber is missing" in {
    val dto      = validAmendmentDetail.copy(chamber = None, amendmentType = Some("SUAMDT"))
    val Right(a) = dto.toDO(): @unchecked
    a.chamber shouldBe Chamber.Senate
  }

  it should "fail when both chamber and amendmentType are missing — can't satisfy NOT NULL" in {
    val result = validAmendmentDetail.copy(chamber = None, amendmentType = None).toDO()
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("Cannot resolve chamber")) shouldBe Left(true)
  }

  it should "handle partial amendedBill (missing fields)" in {
    val dto = validAmendmentDetail.copy(
      amendedBill = Some(AmendedBillDTO(Some(118), None, None, None, None, Some("hr"), None, None))
    )
    val Right(a) = dto.toDO(): @unchecked
    a.billId shouldBe None // number is None, so billId can't be constructed
  }

  it should "substitute resolved billId / sponsorMemberId / parentAmendmentId and derive Member type" in {
    val Right(a) = validAmendmentDetail.toDO(
      billId = Some(42L),
      sponsorMemberId = Some(7L),
      parentAmendmentId = Some(99L),
    ): @unchecked
    val _ = a.billId shouldBe Some(42L)
    val _ = a.sponsorMemberId shouldBe Some(7L)
    val _ = a.sponsorCommitteeId shouldBe None
    val _ = a.sponsorType shouldBe Some(SponsorType.Member)
    a.parentAmendmentId shouldBe Some(99L)
  }

  it should "derive Committee type from sponsorCommitteeId" in {
    val Right(a) = validAmendmentDetail.toDO(
      billId = Some(42L),
      sponsorCommitteeId = Some(15L),
    ): @unchecked
    val _ = a.sponsorMemberId shouldBe None
    val _ = a.sponsorCommitteeId shouldBe Some(15L)
    a.sponsorType shouldBe Some(SponsorType.Committee)
  }

  it should "fail when both sponsorMemberId and sponsorCommitteeId are provided" in {
    val result = validAmendmentDetail.toDO(
      sponsorMemberId = Some(7L),
      sponsorCommitteeId = Some(15L),
    )
    val _ = result.isLeft shouldBe true
    result.left.map(_.contains("mutually exclusive")) shouldBe Left(true)
  }

  it should "derive sponsorType from DTO when no sponsor IDs are provided" in {
    // Fixture has MemberSponsorDTO → derives Member even without resolved ID
    val Right(a) = validAmendmentDetail.toDO(): @unchecked
    a.sponsorType shouldBe Some(SponsorType.Member)
  }

  it should "populate proposedDate identically with explicit args" in {
    val Right(a) = validAmendmentDetail.toDO(
      billId = Some(1L),
      sponsorMemberId = Some(2L),
      parentAmendmentId = Some(3L),
    ): @unchecked
    a.proposedDate shouldBe Some(LocalDate.parse("2024-02-16"))
  }

  it should "still validate congress / number / amendmentType / chamber with explicit args" in {
    val r = validAmendmentDetail
      .copy(congress = 0)
      .toDO(billId = Some(1L), sponsorMemberId = Some(2L), parentAmendmentId = Some(3L))
    val _ = r.isLeft shouldBe true

    val r2 = validAmendmentDetail.copy(amendmentType = Some("BADTYPE")).toDO(billId = Some(1L))
    r2.isLeft shouldBe true
  }

  "buildAmendmentId" should "construct correct natural key" in {
    val _ = AmendmentConversions.buildAmendmentId(118, Some("HAMDT"), "100") shouldBe "118-HAMDT-100"
    AmendmentConversions.buildAmendmentId(117, None, "50") shouldBe "117-UNKNOWN-50"
  }

  "buildBillIdFromAmendedBill" should "construct billId when all fields present" in {
    AmendmentConversions
      .buildBillIdFromAmendedBill(Some(118), Some("hr"), Some("1234")) shouldBe Some("118-HR-1234")
  }

  it should "return None when any field is missing" in {
    val _ = AmendmentConversions.buildBillIdFromAmendedBill(None, Some("hr"), Some("1234")) shouldBe None
    val _ = AmendmentConversions.buildBillIdFromAmendedBill(Some(118), None, Some("1234")) shouldBe None
    AmendmentConversions.buildBillIdFromAmendedBill(Some(118), Some("hr"), None) shouldBe None
  }

}
