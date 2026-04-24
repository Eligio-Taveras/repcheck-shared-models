package repcheck.shared.models.congress.dto.conversions

import java.time.Instant

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.{Chamber, Party, UsState}
import repcheck.shared.models.congress.dto.common.PaginationInfoDTO
import repcheck.shared.models.congress.dto.conversions.MemberConversions._
import repcheck.shared.models.congress.dto.member._

class MemberConversionsSpec extends AnyFlatSpec with Matchers {

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
    val Right(result) = validMemberDetail.toDO: @unchecked
    val m             = result.member
    val _             = m.memberId shouldBe 0L
    val _             = m.naturalKey shouldBe "S000033"
    val _             = m.firstName shouldBe Some("Bernard")
    val _             = m.lastName shouldBe Some("Sanders")
    val _             = m.directOrderName shouldBe Some("Bernard Sanders")
    val _             = m.invertedOrderName shouldBe Some("Sanders, Bernard")
    val _             = m.honorificName shouldBe Some("Sen.")
    val _             = m.birthYear shouldBe Some(1941)
    val _             = m.state shouldBe Some(UsState.Vermont)
    val _             = m.imageUrl shouldBe Some("https://photo.url")
    val _             = m.imageAttribution shouldBe Some("Senate photo")
    m.updateDate shouldBe Some(Instant.parse("2024-06-15T00:00:00Z"))
  }

  it should "derive currentParty from last partyHistory entry" in {
    val Right(result) = validMemberDetail.toDO: @unchecked
    result.member.currentParty shouldBe Some(Party.Independent)
  }

  it should "derive district from last term" in {
    val Right(result) = validMemberDetail.toDO: @unchecked
    result.member.district shouldBe None // last term has no district (Senate)
  }

  it should "produce terms list with 0L PKs" in {
    val Right(result) = validMemberDetail.toDO: @unchecked
    val _             = result.terms.length shouldBe 2
    result.terms.foreach { t =>
      val _ = t.termId shouldBe 0L
      t.memberId shouldBe 0L
    }
    result.terms.map(_.chamber) shouldBe List(Some(Chamber.House), Some(Chamber.Senate))
  }

  it should "produce partyHistory list" in {
    val Right(result) = validMemberDetail.toDO: @unchecked
    val _             = result.partyHistory.length shouldBe 2
    result.partyHistory.foreach { ph =>
      val _ = ph.id shouldBe 0L
      ph.memberId shouldBe 0L
    }
    result.partyHistory.map(_.partyAbbreviation) shouldBe List(Some("D"), Some("I"))
  }

  it should "populate partyHistory[].partyName with party_type values ('Democrat', not 'D' or 'Democratic')" in {
    // Regression guard: the MemberPartyHistoryDO.partyName field lands in
    // `member_party_history.party_name` which is typed as `party_type` ({Democrat, Republican,
    // Independent}). Congress.gov returns 'Democratic' in partyHistory[].partyName; our conversion
    // must map 'Democratic' -> 'Democrat' so the DB cast succeeds. Unknown values would have failed
    // with `invalid input value for enum party_type` during docker-compose E2E runs before this fix.
    val Right(result) = validMemberDetail.toDO: @unchecked
    result.partyHistory.map(_.partyName) shouldBe List(Some("Democrat"), Some("Independent"))
  }

  "MemberConversions.partyNameToPartyType" should "map 'Democratic' to 'Democrat'" in {
    partyNameToPartyType(Some("Democratic")) shouldBe Some("Democrat")
  }

  it should "pass 'Republican' and 'Independent' through unchanged" in {
    val _ = partyNameToPartyType(Some("Republican")) shouldBe Some("Republican")
    partyNameToPartyType(Some("Independent")) shouldBe Some("Independent")
  }

  it should "pass unknown values through unchanged (DB cast will fail fast)" in {
    partyNameToPartyType(Some("Libertarian")) shouldBe Some("Libertarian")
  }

  it should "return None for None input" in {
    partyNameToPartyType(None) shouldBe None
  }

  "MemberConversions.normalizePartyName" should "still map full party names to single-letter abbreviations" in {
    // Kept available for writers that target `party_abbreviation_type` ({D, R, I}) columns even
    // though the MemberPartyHistoryDO conversion no longer uses it — partyNameToPartyType does.
    val _ = normalizePartyName(Some("Democratic")) shouldBe Some("D")
    val _ = normalizePartyName(Some("Republican")) shouldBe Some("R")
    normalizePartyName(Some("Independent")) shouldBe Some("I")
  }

  it should "fail when bioguideId is empty" in {
    val result = validMemberDetail.copy(bioguideId = "").toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("bioguideId")) shouldBe Left(true)
  }

  it should "fail when bioguideId is blank" in {
    val result = validMemberDetail.copy(bioguideId = "   ").toDO
    result.isLeft shouldBe true
  }

  it should "handle None terms and partyHistory" in {
    val dto           = validMemberDetail.copy(terms = None, partyHistory = None)
    val Right(result) = dto.toDO: @unchecked
    val _             = result.terms shouldBe List.empty
    val _             = result.partyHistory shouldBe List.empty
    val _             = result.member.currentParty shouldBe None
    result.member.district shouldBe None
  }

  it should "handle None depiction" in {
    val dto           = validMemberDetail.copy(depiction = None)
    val Right(result) = dto.toDO: @unchecked
    val _             = result.member.imageUrl shouldBe None
    result.member.imageAttribution shouldBe None
  }

  it should "fail when birthYear is not a valid integer" in {
    val dto    = validMemberDetail.copy(birthYear = Some("not-a-number"))
    val result = dto.toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("birthYear")) shouldBe Left(true)
  }

  it should "handle None birthYear" in {
    val dto           = validMemberDetail.copy(birthYear = None)
    val Right(result) = dto.toDO: @unchecked
    result.member.birthYear shouldBe None
  }

  it should "fail when party abbreviation is unrecognized" in {
    val dto = validMemberDetail.copy(
      partyHistory = Some(List(PartyHistoryDTO(Some("X"), Some("Unknown Party"), Some(2000))))
    )
    val result = dto.toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("Unrecognized Party")) shouldBe Left(true)
  }

  it should "fail when state is unrecognized" in {
    val dto    = validMemberDetail.copy(state = Some("Atlantis"))
    val result = dto.toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("Unrecognized US state")) shouldBe Left(true)
  }

  it should "handle None state" in {
    val dto           = validMemberDetail.copy(state = None)
    val Right(result) = dto.toDO: @unchecked
    result.member.state shouldBe None
  }

  it should "parse state from 2-letter code" in {
    val dto           = validMemberDetail.copy(state = Some("VT"))
    val Right(result) = dto.toDO: @unchecked
    result.member.state shouldBe Some(UsState.Vermont)
  }

  it should "parse currentParty from full name" in {
    val dto = validMemberDetail.copy(
      partyHistory = Some(List(PartyHistoryDTO(Some("Republican"), Some("Republican"), Some(2000))))
    )
    val Right(result) = dto.toDO: @unchecked
    result.member.currentParty shouldBe Some(Party.Republican)
  }

  it should "handle terms with None chamber, memberType, and stateCode" in {
    val sparseTerm    = MemberDetailTermDTO(None, Some(118), Some(2025), None, Some(2007), None, None, None)
    val dto           = validMemberDetail.copy(terms = Some(List(sparseTerm)))
    val Right(result) = dto.toDO: @unchecked
    val _             = result.terms.length shouldBe 1
    val term          = result.terms.headOption
    val _             = term.flatMap(_.chamber) shouldBe None
    val _             = term.flatMap(_.memberType) shouldBe None
    term.flatMap(_.stateCode) shouldBe None
  }

  it should "fail when a term has an unrecognized chamber" in {
    val badTerm =
      MemberDetailTermDTO(Some("InvalidChamber"), Some(118), Some(2025), Some("sen"), Some(2007), None, None, None)
    val dto    = validMemberDetail.copy(terms = Some(List(badTerm)))
    val result = dto.toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("InvalidChamber")) shouldBe Left(true)
  }

  it should "fail when a term has an unrecognized memberType" in {
    val badTerm =
      MemberDetailTermDTO(Some("Senate"), Some(118), Some(2025), Some("governor"), Some(2007), None, None, None)
    val dto    = validMemberDetail.copy(terms = Some(List(badTerm)))
    val result = dto.toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("governor")) shouldBe Left(true)
  }

  it should "fail when a term has an unrecognized stateCode" in {
    val badTerm =
      MemberDetailTermDTO(Some("Senate"), Some(118), Some(2025), Some("sen"), Some(2007), Some("ZZ"), None, None)
    val dto    = validMemberDetail.copy(terms = Some(List(badTerm)))
    val result = dto.toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("ZZ")) shouldBe Left(true)
  }

}
