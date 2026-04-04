package repcheck.shared.models.congress.dto.vote

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class VoteDTOsSpec extends AnyFlatSpec with Matchers {

  "VoteResultDTO" should "round-trip" in {
    val dto = VoteResultDTO(
      memberId = Some("B001297"),
      firstName = Some("Ken"),
      lastName = Some("Buck"),
      voteCast = Some("Yea"),
      party = Some("R"),
      state = Some("CO"),
    )
    decode[VoteResultDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with all None fields" in {
    decode[VoteResultDTO]("{}") shouldBe Right(VoteResultDTO(None, None, None, None, None, None))
  }

  "VotePartyTotalDTO" should "round-trip" in {
    val dto = VotePartyTotalDTO(
      voteParty = Some("Republican"),
      party = Some("R"),
      yeaTotal = Some(200),
      nayTotal = Some(15),
      presentTotal = Some(3),
      notVotingTotal = Some(7),
    )
    decode[VotePartyTotalDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "VoteListItemDTO" should "round-trip with all fields" in {
    val dto = VoteListItemDTO(
      congress = 118,
      chamber = "House",
      rollCallNumber = 42,
      sessionNumber = Some(1),
      startDate = Some("2024-01-15T14:30:00Z"),
      updateDate = Some("2024-01-16"),
      result = Some("Passed"),
      voteType = Some("YEA-AND-NAY"),
      legislationNumber = Some("HR 1234"),
      legislationType = Some("HR"),
      legislationUrl = Some("https://congress.gov/bill/118/hr/1234"),
      url = Some("https://api.congress.gov/v3/vote/118/house/42"),
    )
    decode[VoteListItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with only required fields" in {
    val json   = """{"congress":118,"chamber":"Senate","rollCallNumber":10}"""
    val result = decode[VoteListItemDTO](json)
    result.isRight shouldBe true
    result.map(_.sessionNumber) shouldBe Right(None)
  }

  "VoteDetailDTO" should "round-trip" in {
    val dto = VoteDetailDTO(
      congress = 118,
      chamber = "Senate",
      rollCallNumber = 99,
      sessionNumber = Some(2),
      startDate = Some("2024-06-01"),
      updateDate = Some("2024-06-02"),
      result = Some("Agreed to"),
      voteType = Some("RECORDED VOTE"),
      legislationNumber = None,
      legislationType = None,
      legislationUrl = None,
      url = Some("https://api.congress.gov/v3/vote/118/senate/99"),
      voteQuestion = Some("On the Motion"),
      votePartyTotal = Some(
        List(
          VotePartyTotalDTO(Some("Democratic"), Some("D"), Some(48), Some(0), Some(1), Some(1))
        )
      ),
    )
    decode[VoteDetailDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "VoteMembersDTO" should "round-trip" in {
    val dto = VoteMembersDTO(
      congress = 118,
      chamber = "House",
      rollCallNumber = 50,
      sessionNumber = Some(1),
      startDate = Some("2024-03-01"),
      updateDate = None,
      result = Some("Passed"),
      voteType = Some("YEA-AND-NAY"),
      legislationNumber = Some("HR 5000"),
      legislationType = Some("HR"),
      legislationUrl = None,
      url = Some("https://api.congress.gov/v3/vote/118/house/50"),
      voteQuestion = Some("On Passage"),
      results = Some(
        List(
          VoteResultDTO(Some("A000370"), Some("Alma"), Some("Adams"), Some("Yea"), Some("D"), Some("NC")),
          VoteResultDTO(Some("B001297"), Some("Ken"), Some("Buck"), Some("Nay"), Some("R"), Some("CO")),
        )
      ),
    )
    decode[VoteMembersDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

}
