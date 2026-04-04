package repcheck.shared.models.congress.dto.conversions

import java.util.UUID

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.common.PaginationInfoDTO
import repcheck.shared.models.congress.dto.conversions.MemberConversions._
import repcheck.shared.models.congress.dto.member._

class MemberConversionsSpec extends AnyFlatSpec with Matchers {

  private val fixedUuid           = UUID.fromString("00000000-0000-0000-0000-000000000001")
  private val uuidGen: () => UUID = () => fixedUuid

  private val validMemberDetail = MemberDetailDTO(
    bioguideId = "S000033",
    birthYear = Some("1941"),
    firstName = Some("Bernard"),
    lastName = Some("Sanders"),
    directOrderName = Some("Bernard Sanders"),
    invertedOrderName = Some("Sanders, Bernard"),
    honorificName = Some("Sen."),
    cosponsoredLegislation = Some(PaginationInfoDTO(Some(500), None)),
    depiction = Some(MemberDepictionDTO(Some("https://photo.url"), Some("Senate photo"))),
    leadership = Some(List(LeadershipDTO(Some(117), Some("Chair")))),
    partyHistory = Some(
      List(
        PartyHistoryDTO(Some("D"), Some("Democratic"), Some(1981)),
        PartyHistoryDTO(Some("I"), Some("Independent"), Some(2007)),
      )
    ),
    sponsoredLegislation = Some(PaginationInfoDTO(Some(300), None)),
    state = Some("Vermont"),
    terms = Some(
      List(
        MemberDetailTermDTO(
          Some("House"),
          Some(102),
          Some(1997),
          Some("rep"),
          Some(1991),
          Some("VT"),
          Some("Vermont"),
          Some(0),
        ),
        MemberDetailTermDTO(
          Some("Senate"),
          Some(118),
          Some(2025),
          Some("sen"),
          Some(2007),
          Some("VT"),
          Some("Vermont"),
          None,
        ),
      )
    ),
    updateDate = Some("2024-06-15"),
  )

  "MemberDetailDTO.toDO" should "produce MemberDO with correct fields" in {
    val Right(result) = validMemberDetail.toDO(uuidGen): @unchecked
    val m             = result.member
    m.memberId shouldBe "S000033"
    m.firstName shouldBe Some("Bernard")
    m.lastName shouldBe Some("Sanders")
    m.directOrderName shouldBe Some("Bernard Sanders")
    m.invertedOrderName shouldBe Some("Sanders, Bernard")
    m.honorificName shouldBe Some("Sen.")
    m.birthYear shouldBe Some("1941")
    m.state shouldBe Some("Vermont")
    m.imageUrl shouldBe Some("https://photo.url")
    m.imageAttribution shouldBe Some("Senate photo")
    m.updateDate shouldBe Some("2024-06-15")
  }

  it should "derive currentParty from last partyHistory entry" in {
    val Right(result) = validMemberDetail.toDO(uuidGen): @unchecked
    result.member.currentParty shouldBe Some("I")
  }

  it should "derive district from last term" in {
    val Right(result) = validMemberDetail.toDO(uuidGen): @unchecked
    result.member.district shouldBe None // last term has no district (Senate)
  }

  it should "produce terms list with generated UUIDs" in {
    val Right(result) = validMemberDetail.toDO(uuidGen): @unchecked
    result.terms.length shouldBe 2
    result.terms.foreach { t =>
      t.termId shouldBe fixedUuid
      t.memberId shouldBe "S000033"
    }
    result.terms.map(_.chamber) shouldBe List(Some("House"), Some("Senate"))
  }

  it should "produce partyHistory list" in {
    val Right(result) = validMemberDetail.toDO(uuidGen): @unchecked
    result.partyHistory.length shouldBe 2
    result.partyHistory.foreach { ph =>
      ph.partyHistoryId shouldBe fixedUuid
      ph.memberId shouldBe "S000033"
    }
    result.partyHistory.map(_.partyAbbreviation) shouldBe List(Some("D"), Some("I"))
  }

  it should "fail when bioguideId is empty" in {
    val result = validMemberDetail.copy(bioguideId = "").toDO(uuidGen)
    result.isLeft shouldBe true
    result.left.map(msg => msg.contains("bioguideId")) shouldBe Left(true)
  }

  it should "fail when bioguideId is blank" in {
    val result = validMemberDetail.copy(bioguideId = "   ").toDO(uuidGen)
    result.isLeft shouldBe true
  }

  it should "handle None terms and partyHistory" in {
    val dto           = validMemberDetail.copy(terms = None, partyHistory = None)
    val Right(result) = dto.toDO(uuidGen): @unchecked
    result.terms shouldBe List.empty
    result.partyHistory shouldBe List.empty
    result.member.currentParty shouldBe None
    result.member.district shouldBe None
  }

  it should "handle None depiction" in {
    val dto           = validMemberDetail.copy(depiction = None)
    val Right(result) = dto.toDO(uuidGen): @unchecked
    result.member.imageUrl shouldBe None
    result.member.imageAttribution shouldBe None
  }

  it should "succeed using default UUID generator" in {
    val result = validMemberDetail.toDO
    result.isRight shouldBe true
    val Right(conv) = result: @unchecked
    conv.member.memberId shouldBe "S000033"
    conv.terms.length shouldBe 2
    conv.partyHistory.length shouldBe 2
  }

}
