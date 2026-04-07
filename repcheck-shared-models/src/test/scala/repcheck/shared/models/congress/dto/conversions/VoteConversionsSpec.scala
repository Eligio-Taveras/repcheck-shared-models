package repcheck.shared.models.congress.dto.conversions

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.conversions.VoteConversions._
import repcheck.shared.models.congress.dto.vote._

class VoteConversionsSpec extends AnyFlatSpec with Matchers {

  private val validVoteMembers = VoteMembersDTO(
    congress = 118,
    chamber = "House",
    rollCallNumber = 42,
    sessionNumber = Some(1),
    startDate = Some("2024-01-15"),
    updateDate = Some("2024-01-16"),
    result = Some("Passed"),
    voteType = Some("YEA-AND-NAY"),
    legislationNumber = Some("HR 1234"),
    legislationType = Some("HR"),
    legislationUrl = Some("https://congress.gov/bill/118/hr/1234"),
    url = Some("https://api.congress.gov/v3/vote/118/house/42"),
    identifier = Some("118-H-42"),
    sourceDataUrl = Some("https://clerk.house.gov/evs/2024/roll042.xml"),
    voteQuestion = Some("On Passage"),
    results = Some(
      List(
        VoteResultDTO(Some("A000370"), Some("Alma"), Some("Adams"), Some("Yea"), Some("D"), Some("NC")),
        VoteResultDTO(Some("B001297"), Some("Ken"), Some("Buck"), Some("Nay"), Some("R"), Some("CO")),
        VoteResultDTO(None, Some("Ghost"), Some("Member"), Some("Present"), Some("I"), Some("VT")),
      )
    ),
  )

  "VoteMembersDTO.toDO" should "produce VoteDO with correct natural key" in {
    val Right(result) = validVoteMembers.toDO: @unchecked
    val _             = result.vote.voteId shouldBe 0L
    result.vote.naturalKey shouldBe "118-House-42"
  }

  it should "map all vote fields correctly" in {
    val Right(result) = validVoteMembers.toDO: @unchecked
    val v             = result.vote
    val _             = v.congress shouldBe 118
    val _             = v.chamber shouldBe "House"
    val _             = v.rollNumber shouldBe 42
    val _             = v.sessionNumber shouldBe Some(1)
    val _             = v.question shouldBe Some("On Passage")
    val _             = v.voteType shouldBe Some("YEA-AND-NAY")
    val _             = v.voteMethod shouldBe None
    val _             = v.result shouldBe Some("Passed")
    val _             = v.voteDate shouldBe Some("2024-01-15")
    val _             = v.legislationNumber shouldBe Some("HR 1234")
    val _             = v.legislationType shouldBe Some("HR")
    val _             = v.legislationUrl shouldBe Some("https://congress.gov/bill/118/hr/1234")
    val _             = v.sourceDataUrl shouldBe Some("https://clerk.house.gov/evs/2024/roll042.xml")
    v.updateDate shouldBe Some("2024-01-16")
  }

  it should "produce VotePositionDOs only for members with memberId in DTO" in {
    val Right(result) = validVoteMembers.toDO: @unchecked
    val _             = result.positions.length shouldBe 2
    result.positions.foreach(_.memberId shouldBe 0L)
  }

  it should "map position fields correctly" in {
    val Right(result) = validVoteMembers.toDO: @unchecked
    val first         = result.positions.headOption
    val _             = first.map(_.voteId) shouldBe Some(0L)
    val _             = first.map(_.memberId) shouldBe Some(0L)
    val _             = first.flatMap(_.position) shouldBe Some("Yea")
    val _             = first.flatMap(_.partyAtVote) shouldBe Some("D")
    first.flatMap(_.stateAtVote) shouldBe Some("NC")
  }

  it should "fail when congress <= 0" in {
    val result = validVoteMembers.copy(congress = 0).toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("congress")) shouldBe Left(true)
  }

  it should "fail when chamber is empty" in {
    val result = validVoteMembers.copy(chamber = "").toDO
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("chamber")) shouldBe Left(true)
  }

  it should "handle None results" in {
    val Right(result) = validVoteMembers.copy(results = None).toDO: @unchecked
    result.positions shouldBe List.empty
  }

  // SenateVoteXmlDTO conversion tests

  private val senateVoteXml = SenateVoteXmlDTO(
    congress = 118,
    session = 1,
    voteNumber = 99,
    question = "On the Motion",
    voteDate = "2024-06-01",
    result = "Motion Agreed to",
    members = List(
      SenateVoteMemberXmlDTO("S0001", "John", "Smith", "D", "NY", "Yea"),
      SenateVoteMemberXmlDTO("S0002", "Jane", "Doe", "R", "TX", "Nay"),
    ),
  )

  private val completeMapping = Map("S0001" -> "B001001", "S0002" -> "B002002")

  "SenateVoteXmlDTO.toDO" should "convert with complete mapping" in {
    val Right(result) = senateVoteXml.toDO(completeMapping): @unchecked
    val _             = result.congress shouldBe 118
    val _             = result.chamber shouldBe "Senate"
    val _             = result.rollCallNumber shouldBe 99
    val _             = result.sessionNumber shouldBe Some(1)
    val _             = result.voteQuestion shouldBe Some("On the Motion")
    val _             = result.result shouldBe Some("Motion Agreed to")
    result.startDate shouldBe Some("2024-06-01")
  }

  it should "resolve lisMemberIds to bioguideIds in results" in {
    val Right(result) = senateVoteXml.toDO(completeMapping): @unchecked
    val members       = result.results.getOrElse(List.empty)
    val _             = members.length shouldBe 2
    val _             = members.flatMap(_.memberId) shouldBe List("B001001", "B002002")
    val _             = members.flatMap(_.firstName) shouldBe List("John", "Jane")
    members.flatMap(_.voteCast) shouldBe List("Yea", "Nay")
  }

  it should "fail with missing mappings listing unresolved IDs" in {
    val partialMapping = Map("S0001" -> "B001001")
    val result         = senateVoteXml.toDO(partialMapping)
    val _              = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("S0002")) shouldBe Left(true)
  }

  it should "fail with empty mapping" in {
    val result = senateVoteXml.toDO(Map.empty)
    val _      = result.isLeft shouldBe true
    val _      = result.left.map(msg => msg.contains("S0001")) shouldBe Left(true)
    result.left.map(msg => msg.contains("S0002")) shouldBe Left(true)
  }

  "buildVoteId" should "construct correct natural key" in {
    val _ = VoteConversions.buildVoteId(118, "House", 42) shouldBe "118-House-42"
    VoteConversions.buildVoteId(117, "Senate", 100) shouldBe "117-Senate-100"
  }

}
