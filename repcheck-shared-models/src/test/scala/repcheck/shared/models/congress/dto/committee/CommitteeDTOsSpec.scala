package repcheck.shared.models.congress.dto.committee

import io.circe.parser.{decode, decodeAccumulating}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommitteeDTOsSpec extends AnyFlatSpec with Matchers {

  "SenatorCommitteeAssignmentXmlDTO" should "round-trip" in {
    val dto = SenatorCommitteeAssignmentXmlDTO(
      committeeCode = "SSFI00",
      committeeName = "Committee on Finance",
      position = "Chairman",
    )
    decode[SenatorCommitteeAssignmentXmlDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "SenatorCommitteeDataXmlDTO" should "round-trip with all fields" in {
    val dto = SenatorCommitteeDataXmlDTO(
      lisMemberId = "S123",
      bioguideId = "W000779",
      firstName = "Ron",
      lastName = "Wyden",
      party = "D",
      state = "OR",
      stateRank = Some("senior"),
      office = Some("221 Dirksen"),
      leadershipPosition = Some("Chair"),
      committees = List(
        SenatorCommitteeAssignmentXmlDTO("SSFI00", "Committee on Finance", "Chairman")
      ),
    )
    decode[SenatorCommitteeDataXmlDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "round-trip with optional fields as None" in {
    val dto = SenatorCommitteeDataXmlDTO(
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
    decode[SenatorCommitteeDataXmlDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "HouseCommitteeAssignmentXmlDTO" should "round-trip" in {
    val dto = HouseCommitteeAssignmentXmlDTO(
      committeeCode = "HSJU00",
      committeeName = "Committee on the Judiciary",
      rank = Some(1),
      side = "majority",
    )
    decode[HouseCommitteeAssignmentXmlDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "round-trip with rank None" in {
    val dto = HouseCommitteeAssignmentXmlDTO(
      committeeCode = "HSJU00",
      committeeName = "Committee on the Judiciary",
      rank = None,
      side = "minority",
    )
    decode[HouseCommitteeAssignmentXmlDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "HouseMemberDataXmlDTO" should "round-trip with all fields" in {
    val dto = HouseMemberDataXmlDTO(
      bioguideId = "P000197",
      firstName = "Nancy",
      lastName = "Pelosi",
      party = "D",
      state = "CA",
      district = Some(11),
      committees = List(
        HouseCommitteeAssignmentXmlDTO("HSAP00", "Committee on Appropriations", Some(2), "majority")
      ),
    )
    decode[HouseMemberDataXmlDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "round-trip with district None and empty committees" in {
    val dto = HouseMemberDataXmlDTO(
      bioguideId = "X000001",
      firstName = "Test",
      lastName = "Member",
      party = "R",
      state = "TX",
      district = None,
      committees = List.empty,
    )
    decode[HouseMemberDataXmlDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "CommitteeActivityDTO" should "round-trip" in {
    val dto = CommitteeActivityDTO(name = "Referred to", date = Some("2024-03-15"))
    decode[CommitteeActivityDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "round-trip with date None" in {
    val dto = CommitteeActivityDTO(name = "Reported by", date = None)
    decode[CommitteeActivityDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "BillCommitteeReferralDTO" should "round-trip with all fields" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "SSFI00",
      committeeName = "Committee on Finance",
      chamber = Some("Senate"),
      activities = List(
        CommitteeActivityDTO("Referred to", Some("2024-01-10")),
        CommitteeActivityDTO("Reported by", Some("2024-03-20")),
      ),
    )
    decode[BillCommitteeReferralDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "round-trip with chamber None and empty activities" in {
    val dto = BillCommitteeReferralDTO(
      committeeCode = "HSJU00",
      committeeName = "Committee on the Judiciary",
      chamber = None,
      activities = List.empty,
    )
    decode[BillCommitteeReferralDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "PaginationCountDTO" should "round-trip" in {
    val dto = PaginationCountDTO(count = Some(42), url = Some("https://api.congress.gov/v3/committee/bills"))
    decode[PaginationCountDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "round-trip with all fields None" in {
    val dto = PaginationCountDTO(count = None, url = None)
    decode[PaginationCountDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "CommitteeListItemDTO" should "round-trip with all fields" in {
    val parentDto = CommitteeListItemDTO(
      chamber = Some("Senate"),
      committeeTypeCode = Some("Standing"),
      name = "Committee on Finance",
      systemCode = "ssfi00",
      updateDate = Some("2024-06-01"),
      url = Some("https://api.congress.gov/v3/committee/ssfi00"),
      parent = None,
      subcommittees = None,
    )
    val dto = CommitteeListItemDTO(
      chamber = Some("Senate"),
      committeeTypeCode = Some("Subcommittee"),
      name = "Subcommittee on Health Care",
      systemCode = "ssfi09",
      updateDate = Some("2024-05-01"),
      url = Some("https://api.congress.gov/v3/committee/ssfi09"),
      parent = Some(parentDto),
      subcommittees = None,
    )
    decode[CommitteeListItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "round-trip with all optional fields None" in {
    val dto = CommitteeListItemDTO(
      chamber = None,
      committeeTypeCode = None,
      name = "Test Committee",
      systemCode = "test00",
      updateDate = None,
      url = None,
      parent = None,
      subcommittees = None,
    )
    decode[CommitteeListItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "round-trip with subcommittees" in {
    val sub = CommitteeListItemDTO(
      chamber = None,
      committeeTypeCode = None,
      name = "Sub A",
      systemCode = "sub01",
      updateDate = None,
      url = None,
      parent = None,
      subcommittees = None,
    )
    val dto = CommitteeListItemDTO(
      chamber = Some("House"),
      committeeTypeCode = Some("Standing"),
      name = "Main Committee",
      systemCode = "main00",
      updateDate = None,
      url = None,
      parent = None,
      subcommittees = Some(List(sub)),
    )
    decode[CommitteeListItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "CommitteeDetailDTO" should "round-trip with all fields" in {
    val dto = CommitteeDetailDTO(
      systemCode = "ssfi00",
      `type` = Some("Standing"),
      isCurrent = Some(true),
      history = Some(List("Established in 1816", "Reorganized in 1977")),
      bills = Some(PaginationCountDTO(Some(150), Some("https://api.congress.gov/v3/committee/ssfi00/bills"))),
      reports = Some(PaginationCountDTO(Some(30), Some("https://api.congress.gov/v3/committee/ssfi00/reports"))),
      subcommittees = Some(
        List(
          CommitteeListItemDTO(None, None, "Subcommittee on Health Care", "ssfi09", None, None, None, None)
        )
      ),
    )
    decode[CommitteeDetailDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "round-trip with all optional fields None" in {
    val dto = CommitteeDetailDTO(
      systemCode = "test00",
      `type` = None,
      isCurrent = None,
      history = None,
      bills = None,
      reports = None,
      subcommittees = None,
    )
    decode[CommitteeDetailDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "SenatorCommitteeAssignmentXmlDTO decodeAccumulating" should "succeed on valid JSON" in {
    val json = """{"committeeCode":"SSFI00","committeeName":"Finance","position":"Member"}"""
    decodeAccumulating[SenatorCommitteeAssignmentXmlDTO](json).isValid shouldBe true
  }

  "SenatorCommitteeDataXmlDTO decodeAccumulating" should "succeed on valid JSON" in {
    val json =
      """{"lisMemberId":"S123","bioguideId":"B001","firstName":"A","lastName":"B","party":"D","state":"OR","committees":[]}"""
    decodeAccumulating[SenatorCommitteeDataXmlDTO](json).isValid shouldBe true
  }

  "HouseCommitteeAssignmentXmlDTO decodeAccumulating" should "succeed on valid JSON" in {
    val json = """{"committeeCode":"HSJU00","committeeName":"Judiciary","side":"majority"}"""
    decodeAccumulating[HouseCommitteeAssignmentXmlDTO](json).isValid shouldBe true
  }

  "HouseMemberDataXmlDTO decodeAccumulating" should "succeed on valid JSON" in {
    val json =
      """{"bioguideId":"P000197","firstName":"Nancy","lastName":"Pelosi","party":"D","state":"CA","committees":[]}"""
    decodeAccumulating[HouseMemberDataXmlDTO](json).isValid shouldBe true
  }

  "CommitteeActivityDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[CommitteeActivityDTO]("""{"name":"Referred to"}""").isValid shouldBe true
  }

  "BillCommitteeReferralDTO decodeAccumulating" should "succeed on valid JSON" in {
    val json = """{"committeeCode":"SSFI00","committeeName":"Finance","activities":[]}"""
    decodeAccumulating[BillCommitteeReferralDTO](json).isValid shouldBe true
  }

  "PaginationCountDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[PaginationCountDTO]("""{}""").isValid shouldBe true
  }

  "CommitteeListItemDTO decodeAccumulating" should "succeed on valid JSON" in {
    val json = """{"name":"Test","systemCode":"test00"}"""
    decodeAccumulating[CommitteeListItemDTO](json).isValid shouldBe true
  }

  "CommitteeDetailDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[CommitteeDetailDTO]("""{"systemCode":"test00"}""").isValid shouldBe true
  }

}
