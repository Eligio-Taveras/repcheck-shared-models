package repcheck.shared.models.congress.dto.conversions

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.committee._
import repcheck.shared.models.congress.dto.conversions.CommitteeConversions._

class CommitteeConversionsSpec extends AnyFlatSpec with Matchers {

  private val senatorWithCommittees = SenatorCommitteeDataXmlDTO(
    lisMemberId = "S123",
    bioguideId = "W000779",
    firstName = "Ron",
    lastName = "Wyden",
    party = "D",
    state = "OR",
    stateRank = Some("senior"),
    office = Some("221 Dirksen"),
    leadershipPosition = None,
    committees = List(
      SenatorCommitteeAssignmentXmlDTO("SSFI00", "Committee on Finance", "Chairman"),
      SenatorCommitteeAssignmentXmlDTO("SSBK00", "Committee on Banking", "Member"),
    ),
  )

  private val senatorNoCommittees = SenatorCommitteeDataXmlDTO(
    lisMemberId = "S456",
    bioguideId = "T000250",
    firstName = "John",
    lastName = "Thune",
    party = "R",
    state = "SD",
    stateRank = None,
    office = None,
    leadershipPosition = None,
    committees = List.empty,
  )

  private val houseMember = HouseMemberDataXmlDTO(
    bioguideId = "P000197",
    firstName = "Nancy",
    lastName = "Pelosi",
    party = "D",
    state = "CA",
    district = Some(11),
    committees = List(
      HouseCommitteeAssignmentXmlDTO("HSAP00", "Committee on Appropriations", Some(2), "majority"),
      HouseCommitteeAssignmentXmlDTO("HSJU00", "Committee on the Judiciary", None, "minority"),
    ),
  )

  "SenatorCommitteeDataXmlDTO.toMemberCommittees" should "produce correct number of CommitteeMemberDOs" in {
    val result = senatorWithCommittees.toMemberCommittees
    result.length shouldBe 2
  }

  it should "set committeeId and memberId to 0L (FK resolved at persistence time)" in {
    val result = senatorWithCommittees.toMemberCommittees
    result.foreach(_.committeeId shouldBe 0L)
    result.foreach(_.memberId shouldBe 0L)
  }

  it should "map position from assignment" in {
    val result = senatorWithCommittees.toMemberCommittees
    result.map(_.position) shouldBe List(Some("Chairman"), Some("Member"))
  }

  it should "set side to None for senate assignments" in {
    val result = senatorWithCommittees.toMemberCommittees
    result.foreach(_.side shouldBe None)
  }

  it should "return empty list for senator with no committees" in {
    val result = senatorNoCommittees.toMemberCommittees
    result shouldBe List.empty
  }

  "SenatorCommitteeDataXmlDTO.toLisMemberMapping" should "produce Some with memberId set to 0L (FK resolved at persistence time)" in {
    val result = senatorWithCommittees.toLisMemberMapping
    result.map(_.memberId) shouldBe Some(0L)
  }

  it should "produce Some with correct lisMemberId" in {
    val result = senatorWithCommittees.toLisMemberMapping
    result.map(_.lisMemberId) shouldBe Some("S123")
  }

  it should "produce Some even for senator with no committees" in {
    val result = senatorNoCommittees.toLisMemberMapping
    result.map(_.memberId) shouldBe Some(0L)
    result.map(_.lisMemberId) shouldBe Some("S456")
  }

  it should "set lastVerified to approximately now" in {
    val before = java.time.Instant.now()
    val result = senatorNoCommittees.toLisMemberMapping
    val after  = java.time.Instant.now()
    result.foreach { mapping =>
      mapping.lastVerified.compareTo(before) should be >= 0
      mapping.lastVerified.compareTo(after) should be <= 0
    }
    result.isDefined shouldBe true
  }

  it should "return None when lisMemberId is blank" in {
    val dto = senatorWithCommittees.copy(lisMemberId = "")
    dto.toLisMemberMapping shouldBe None
  }

  it should "return None when lisMemberId is whitespace only" in {
    val dto = senatorWithCommittees.copy(lisMemberId = "   ")
    dto.toLisMemberMapping shouldBe None
  }

  it should "return None when bioguideId is blank" in {
    val dto = senatorWithCommittees.copy(bioguideId = "")
    dto.toLisMemberMapping shouldBe None
  }

  it should "return None when bioguideId is whitespace only" in {
    val dto = senatorWithCommittees.copy(bioguideId = "   ")
    dto.toLisMemberMapping shouldBe None
  }

  "HouseMemberDataXmlDTO.toMemberCommittees" should "produce correct number of CommitteeMemberDOs" in {
    val result = houseMember.toMemberCommittees
    result.length shouldBe 2
  }

  it should "map side from assignment" in {
    val result = houseMember.toMemberCommittees
    result.map(_.side) shouldBe List(Some("majority"), Some("minority"))
  }

  it should "map rank from assignment" in {
    val result = houseMember.toMemberCommittees
    result.map(_.rank) shouldBe List(Some(2), None)
  }

  it should "set position to None for house assignments" in {
    val result = houseMember.toMemberCommittees
    result.foreach(_.position shouldBe None)
  }

  it should "return empty list for house member with no committees" in {
    val dto = houseMember.copy(committees = List.empty)
    dto.toMemberCommittees shouldBe List.empty
  }

  "CommitteeListItemDTO.toDO" should "map systemCode to naturalKey" in {
    val dto = CommitteeListItemDTO(
      chamber = Some("Senate"),
      committeeTypeCode = Some("Standing"),
      name = "Committee on Finance",
      systemCode = "ssfi00",
      updateDate = Some("2024-06-01"),
      url = Some("https://example.com"),
      parent = None,
      subcommittees = None,
    )
    val Right(result) = dto.toDO: @unchecked
    result.committeeId shouldBe 0L
    result.naturalKey shouldBe "ssfi00"
    result.name shouldBe "Committee on Finance"
    result.chamber shouldBe Some("Senate")
    result.committeeType shouldBe Some("Standing")
  }

  it should "set parentCommitteeId to None (FK resolved at persistence time)" in {
    val parent = CommitteeListItemDTO(
      chamber = None,
      committeeTypeCode = None,
      name = "Parent",
      systemCode = "ssfi00",
      updateDate = None,
      url = None,
      parent = None,
      subcommittees = None,
    )
    val dto = CommitteeListItemDTO(
      chamber = None,
      committeeTypeCode = None,
      name = "Sub",
      systemCode = "ssfi09",
      updateDate = None,
      url = None,
      parent = Some(parent),
      subcommittees = None,
    )
    val Right(result) = dto.toDO: @unchecked
    result.parentCommitteeId shouldBe None
  }

  it should "fail when systemCode is blank" in {
    val dto = CommitteeListItemDTO(
      chamber = None,
      committeeTypeCode = None,
      name = "Bad Committee",
      systemCode = "",
      updateDate = None,
      url = None,
      parent = None,
      subcommittees = None,
    )
    val result = dto.toDO
    result.isLeft shouldBe true
    result.left.map(_.contains("systemCode")) shouldBe Left(true)
  }

  it should "fail when systemCode is whitespace only" in {
    val dto = CommitteeListItemDTO(
      chamber = None,
      committeeTypeCode = None,
      name = "Bad Committee",
      systemCode = "   ",
      updateDate = None,
      url = None,
      parent = None,
      subcommittees = None,
    )
    dto.toDO.isLeft shouldBe true
  }

  "BillCommitteeReferralDTO.toDO" should "extract referralDate from Referred to activity" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "SSFI00",
      committeeName = "Finance",
      chamber = Some("Senate"),
      activities = List(
        CommitteeActivityDTO("Referred to", Some("2024-01-10")),
        CommitteeActivityDTO("Hearings by", Some("2024-02-15")),
      ),
    )
    val Right(result) = dto.toDO(0L): @unchecked
    result.referralDate shouldBe Some("2024-01-10")
    result.reportDate shouldBe None
  }

  it should "extract reportDate from Reported by activity" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "SSFI00",
      committeeName = "Finance",
      chamber = Some("Senate"),
      activities = List(
        CommitteeActivityDTO("Referred to", Some("2024-01-10")),
        CommitteeActivityDTO("Reported by", Some("2024-03-20")),
      ),
    )
    val Right(result) = dto.toDO(0L): @unchecked
    result.referralDate shouldBe Some("2024-01-10")
    result.reportDate shouldBe Some("2024-03-20")
  }

  it should "select earliest date when multiple Referred to activities exist" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "SSFI00",
      committeeName = "Finance",
      chamber = None,
      activities = List(
        CommitteeActivityDTO("Referred to", Some("2024-03-01")),
        CommitteeActivityDTO("Referred to", Some("2024-01-15")),
      ),
    )
    val Right(result) = dto.toDO(0L): @unchecked
    result.referralDate shouldBe Some("2024-01-15")
  }

  it should "set billId and committeeId to 0L (FK resolved at persistence time)" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "HSJU00",
      committeeName = "Judiciary",
      chamber = None,
      activities = List.empty,
    )
    val Right(result) = dto.toDO(0L): @unchecked
    result.billId shouldBe 0L
    result.committeeId shouldBe 0L
  }

  it should "set referralDate and reportDate to None when no matching activities" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "HSJU00",
      committeeName = "Judiciary",
      chamber = None,
      activities = List(
        CommitteeActivityDTO("Hearings by", Some("2024-02-01"))
      ),
    )
    val Right(result) = dto.toDO(0L): @unchecked
    result.referralDate shouldBe None
    result.reportDate shouldBe None
  }

  it should "concatenate activity names" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "SSFI00",
      committeeName = "Finance",
      chamber = None,
      activities = List(
        CommitteeActivityDTO("Referred to", Some("2024-01-10")),
        CommitteeActivityDTO("Reported by", Some("2024-03-20")),
      ),
    )
    val Right(result) = dto.toDO(0L): @unchecked
    result.activity shouldBe Some("Referred to; Reported by")
  }

  it should "set activity to None when activities list is empty" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "SSFI00",
      committeeName = "Finance",
      chamber = None,
      activities = List.empty,
    )
    val Right(result) = dto.toDO(0L): @unchecked
    result.activity shouldBe None
  }

  it should "fail when committeeCode is blank" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "",
      committeeName = "Finance",
      chamber = None,
      activities = List.empty,
    )
    val result = dto.toDO(0L)
    result.isLeft shouldBe true
    result.left.map(_.contains("committeeCode")) shouldBe Left(true)
  }

  it should "fail when committeeCode is whitespace only" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "   ",
      committeeName = "Finance",
      chamber = None,
      activities = List.empty,
    )
    dto.toDO(0L).isLeft shouldBe true
  }

}
