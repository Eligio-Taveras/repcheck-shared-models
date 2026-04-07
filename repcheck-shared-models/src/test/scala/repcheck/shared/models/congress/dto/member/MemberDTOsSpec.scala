package repcheck.shared.models.congress.dto.member

import io.circe.parser.{decode, decodeAccumulating}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.common.PaginationInfoDTO

class MemberDTOsSpec extends AnyFlatSpec with Matchers {

  "MemberDepictionDTO" should "round-trip" in {
    val dto = MemberDepictionDTO(
      imageUrl = Some("https://example.com/photo.jpg"),
      attribution = Some("Courtesy Official Photo"),
    )
    decode[MemberDepictionDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with missing optional fields" in {
    decode[MemberDepictionDTO]("{}") shouldBe Right(MemberDepictionDTO(None, None))
  }

  "MemberTermSummaryDTO" should "round-trip" in {
    val dto = MemberTermSummaryDTO(chamber = Some("House"), startYear = Some(2019))
    decode[MemberTermSummaryDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "MemberDetailTermDTO" should "round-trip with all fields" in {
    val dto = MemberDetailTermDTO(
      chamber = Some("Senate"),
      congress = Some(118),
      endYear = Some(2025),
      memberType = Some("sen"),
      startYear = Some(2019),
      stateCode = Some("VT"),
      stateName = Some("Vermont"),
      district = None,
    )
    decode[MemberDetailTermDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "PartyHistoryDTO" should "round-trip" in {
    val dto = PartyHistoryDTO(
      partyAbbreviation = Some("D"),
      partyName = Some("Democratic"),
      startYear = Some(2003),
    )
    decode[PartyHistoryDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "LeadershipDTO" should "encode 'type' field and round-trip" in {
    val dto  = LeadershipDTO(congress = Some(118), type_ = Some("Majority Leader"))
    val json = dto.asJson
    val _    = json.hcursor.downField("type").as[Option[String]] shouldBe Right(Some("Majority Leader"))
    json.as[LeadershipDTO] shouldBe Right(dto)
  }

  it should "decode with missing fields" in {
    decode[LeadershipDTO]("{}") shouldBe Right(LeadershipDTO(None, None))
  }

  it should "decode JSON with 'type' field" in {
    val json   = """{"congress":117,"type":"Speaker"}"""
    val result = decode[LeadershipDTO](json)
    result shouldBe Right(LeadershipDTO(congress = Some(117), type_ = Some("Speaker")))
  }

  "MemberListItemDTO" should "round-trip with all fields" in {
    val dto = MemberListItemDTO(
      bioguideId = "P000197",
      name = Some("Pelosi, Nancy"),
      partyName = Some("Democratic"),
      state = Some("California"),
      depiction = Some(MemberDepictionDTO(Some("https://photo.url"), Some("Official"))),
      terms = Some(List(MemberTermSummaryDTO(Some("House"), Some(1987)))),
      updateDate = Some("2024-01-01"),
      url = Some("https://api.congress.gov/v3/member/P000197"),
    )
    decode[MemberListItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "MemberDetailDTO" should "round-trip with all fields" in {
    val dto = MemberDetailDTO(
      bioguideId = "S000033",
      birthYear = Some("1941"),
      firstName = Some("Bernard"),
      lastName = Some("Sanders"),
      directOrderName = Some("Bernard Sanders"),
      invertedOrderName = Some("Sanders, Bernard"),
      honorificName = Some("Sen."),
      cosponsoredLegislation = Some(PaginationInfoDTO(Some(500), Some("https://api.congress.gov/cosponsored"))),
      depiction = Some(MemberDepictionDTO(Some("https://photo.url"), Some("Senate photo"))),
      leadership = Some(List(LeadershipDTO(Some(117), Some("Chair")))),
      partyHistory = Some(List(PartyHistoryDTO(Some("I"), Some("Independent"), Some(2007)))),
      sponsoredLegislation = Some(PaginationInfoDTO(Some(300), Some("https://api.congress.gov/sponsored"))),
      state = Some("Vermont"),
      terms = Some(
        List(
          MemberDetailTermDTO(
            Some("Senate"),
            Some(118),
            Some(2025),
            Some("sen"),
            Some(2019),
            Some("VT"),
            Some("Vermont"),
            None,
          )
        )
      ),
      updateDate = Some("2024-06-15"),
    )
    decode[MemberDetailDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with only required field" in {
    val json   = """{"bioguideId":"T000001"}"""
    val result = decode[MemberDetailDTO](json)
    val _      = result.isRight shouldBe true
    val _      = result.map(_.bioguideId) shouldBe Right("T000001")
    val _      = result.map(_.firstName) shouldBe Right(None)
    result.map(_.terms) shouldBe Right(None)
  }

  "LeadershipDTO" should "encode with both fields None producing empty object" in {
    val dto  = LeadershipDTO(congress = None, type_ = None)
    val json = dto.asJson
    val _    = json.hcursor.downField("congress").failed shouldBe true
    val _    = json.hcursor.downField("type").failed shouldBe true
    json.as[LeadershipDTO] shouldBe Right(dto)
  }

  it should "encode with only congress Some" in {
    val dto  = LeadershipDTO(congress = Some(115), type_ = None)
    val json = dto.asJson
    val _    = json.hcursor.downField("congress").as[Option[Int]] shouldBe Right(Some(115))
    val _    = json.hcursor.downField("type").failed shouldBe true
    json.as[LeadershipDTO] shouldBe Right(dto)
  }

  it should "encode with only type_ Some" in {
    val dto  = LeadershipDTO(congress = None, type_ = Some("Whip"))
    val json = dto.asJson
    val _    = json.hcursor.downField("congress").failed shouldBe true
    val _    = json.hcursor.downField("type").as[Option[String]] shouldBe Right(Some("Whip"))
    json.as[LeadershipDTO] shouldBe Right(dto)
  }

  "MemberTermSummaryDTO" should "round-trip with all fields None" in {
    val dto = MemberTermSummaryDTO(chamber = None, startYear = None)
    decode[MemberTermSummaryDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with absent optional fields" in {
    decode[MemberTermSummaryDTO]("{}") shouldBe Right(MemberTermSummaryDTO(None, None))
  }

  "MemberDetailTermDTO" should "round-trip with district Some" in {
    val dto = MemberDetailTermDTO(
      chamber = Some("House"),
      congress = Some(118),
      endYear = Some(2025),
      memberType = Some("rep"),
      startYear = Some(2023),
      stateCode = Some("CA"),
      stateName = Some("California"),
      district = Some(12),
    )
    decode[MemberDetailTermDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "round-trip with all fields None" in {
    val dto = MemberDetailTermDTO(None, None, None, None, None, None, None, None)
    decode[MemberDetailTermDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "PartyHistoryDTO" should "round-trip with all fields None" in {
    val dto = PartyHistoryDTO(partyAbbreviation = None, partyName = None, startYear = None)
    decode[PartyHistoryDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "MemberListItemDTO" should "round-trip with all optional fields None" in {
    val dto = MemberListItemDTO(
      bioguideId = "X000001",
      name = None,
      partyName = None,
      state = None,
      depiction = None,
      terms = None,
      updateDate = None,
      url = None,
    )
    decode[MemberListItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  "MemberListResponseDTO" should "round-trip via Circe" in {
    val item = MemberListItemDTO("P000197", Some("Pelosi"), None, None, None, None, None, None)
    val resp = MemberListResponseDTO(List(item), Some(PaginationInfoDTO(Some(1), None)))
    decode[MemberListResponseDTO](resp.asJson.noSpaces) shouldBe Right(resp)
  }

  it should "combine via Semigroup" in {
    import cats.Semigroup
    val a = MemberListResponseDTO(
      List(MemberListItemDTO("A", None, None, None, None, None, None, None)),
      Some(PaginationInfoDTO(Some(1), Some("p1"))),
    )
    val b = MemberListResponseDTO(
      List(MemberListItemDTO("B", None, None, None, None, None, None, None)),
      Some(PaginationInfoDTO(Some(1), Some("p2"))),
    )
    val combined = Semigroup[MemberListResponseDTO].combine(a, b)
    val _        = combined.items.map(_.bioguideId) shouldBe List("A", "B")
    combined.pagination shouldBe b.pagination
  }

  "MemberDepictionDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[MemberDepictionDTO]("""{}""").isValid shouldBe true
  }

  "MemberTermSummaryDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[MemberTermSummaryDTO]("""{}""").isValid shouldBe true
  }

  "MemberDetailTermDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[MemberDetailTermDTO]("""{}""").isValid shouldBe true
  }

  "PartyHistoryDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[PartyHistoryDTO]("""{}""").isValid shouldBe true
  }

  "MemberListItemDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[MemberListItemDTO]("""{"bioguideId":"X000001"}""").isValid shouldBe true
  }

  "MemberDetailDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[MemberDetailDTO]("""{"bioguideId":"X000001"}""").isValid shouldBe true
  }

}
