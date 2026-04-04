package repcheck.shared.models.congress.dto.vote

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SenateVoteXmlDTOsSpec extends AnyFlatSpec with Matchers {

  "SenateVoteMemberXmlDTO" should "round-trip" in {
    val dto = SenateVoteMemberXmlDTO(
      lisMemberId = "S0001",
      firstName = "John",
      lastName = "Smith",
      party = "D",
      state = "NY",
      voteCast = "Yea",
    )
    decode[SenateVoteMemberXmlDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "SenateVoteXmlDTO" should "round-trip" in {
    val dto = SenateVoteXmlDTO(
      congress = 118,
      session = 1,
      voteNumber = 42,
      question = "On the Motion",
      voteDate = "2024-01-15",
      result = "Motion Agreed to",
      members = List(
        SenateVoteMemberXmlDTO("S0001", "John", "Smith", "D", "NY", "Yea"),
        SenateVoteMemberXmlDTO("S0002", "Jane", "Doe", "R", "TX", "Nay"),
      ),
    )
    decode[SenateVoteXmlDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "SenateMemberContactDTO" should "round-trip with all fields" in {
    val dto = SenateMemberContactDTO(
      bioguideId = "S000033",
      firstName = "Bernard",
      lastName = "Sanders",
      party = "I",
      state = "VT",
      senateClass = Some(1),
      address = Some("332 Dirksen Senate Office Building"),
      phone = Some("202-224-5141"),
      website = Some("https://www.sanders.senate.gov"),
    )
    decode[SenateMemberContactDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with missing optional fields" in {
    val json   = """{"bioguideId":"T000001","firstName":"Test","lastName":"Person","party":"D","state":"NY"}"""
    val result = decode[SenateMemberContactDTO](json)
    result.isRight shouldBe true
    result.map(_.senateClass) shouldBe Right(None)
    result.map(_.address) shouldBe Right(None)
  }

}
