package repcheck.shared.models.congress.dto.bill

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.common.{PagedObject, PaginationInfoDTO}

class CosponsorListResponseDTOSpec extends AnyFlatSpec with Matchers {

  private val sampleCosponsor = CoSponsorDTO(
    bioguideId = "A000360",
    district = None,
    firstName = Some("Lamar"),
    fullName = Some("Sen. Alexander, Lamar [R-TN]"),
    isOriginalCosponsor = Some(true),
    lastName = Some("Alexander"),
    party = Some("R"),
    sponsorshipDate = Some("2019-01-03"),
    state = Some("TN"),
    url = Some("https://api.congress.gov/v3/member/A000360"),
  )

  private val samplePagination = PaginationInfoDTO(
    count = Some(21),
    url = Some("https://api.congress.gov/v3/bill/116/s/1/cosponsors?offset=20"),
  )

  "CosponsorListResponseDTO" should "decode a valid cosponsor list JSON response" in {
    val json =
      """{
        |  "cosponsors": [
        |    {
        |      "bioguideId": "A000360",
        |      "district": null,
        |      "firstName": "Lamar",
        |      "fullName": "Sen. Alexander, Lamar [R-TN]",
        |      "isOriginalCosponsor": true,
        |      "lastName": "Alexander",
        |      "party": "R",
        |      "sponsorshipDate": "2019-01-03",
        |      "state": "TN",
        |      "url": "https://api.congress.gov/v3/member/A000360"
        |    }
        |  ],
        |  "pagination": {
        |    "count": 21,
        |    "url": "https://api.congress.gov/v3/bill/116/s/1/cosponsors?offset=20"
        |  }
        |}""".stripMargin

    val expected = CosponsorListResponseDTO(
      cosponsors = List(sampleCosponsor),
      pagination = Some(samplePagination),
    )
    decode[CosponsorListResponseDTO](json) shouldBe Right(expected)
  }

  it should "decode with empty cosponsors array" in {
    val json = """{"cosponsors": [], "pagination": {"count": 0}}"""
    decode[CosponsorListResponseDTO](json) shouldBe Right(
      CosponsorListResponseDTO(cosponsors = List.empty, pagination = Some(PaginationInfoDTO(Some(0), None)))
    )
  }

  it should "decode with pagination present" in {
    val json =
      """{
        |  "cosponsors": [],
        |  "pagination": {
        |    "count": 5,
        |    "url": "https://api.congress.gov/v3/bill/116/s/1/cosponsors?offset=20"
        |  }
        |}""".stripMargin

    val result = decode[CosponsorListResponseDTO](json)
    result.map(_.pagination) shouldBe Right(
      Some(PaginationInfoDTO(Some(5), Some("https://api.congress.gov/v3/bill/116/s/1/cosponsors?offset=20")))
    )
  }

  it should "decode with pagination absent" in {
    val json = """{"cosponsors": []}"""
    decode[CosponsorListResponseDTO](json).map(_.pagination) shouldBe Right(None)
  }

  it should "decode with pagination explicitly null" in {
    val json = """{"cosponsors": [], "pagination": null}"""
    decode[CosponsorListResponseDTO](json).map(_.pagination) shouldBe Right(None)
  }

  it should "return cosponsors via items method" in {
    val dto = CosponsorListResponseDTO(
      cosponsors = List(sampleCosponsor),
      pagination = Some(samplePagination),
    )
    dto.items shouldBe dto.cosponsors
  }

  it should "round-trip encode and decode preserving data" in {
    val dto = CosponsorListResponseDTO(
      cosponsors = List(sampleCosponsor),
      pagination = Some(samplePagination),
    )
    dto.asJson.as[CosponsorListResponseDTO] shouldBe Right(dto)
  }

  it should "extend PagedObject[CoSponsorDTO]" in {
    val dto = CosponsorListResponseDTO(
      cosponsors = List(sampleCosponsor),
      pagination = Some(samplePagination),
    )
    val _                                = dto shouldBe a[PagedObject[?]]
    val paged: PagedObject[CoSponsorDTO] = dto
    val _                                = paged.items shouldBe List(sampleCosponsor)
    paged.pagination shouldBe Some(samplePagination)
  }

  it should "combine via Semigroup" in {
    import cats.syntax.semigroup._
    val page1 = CosponsorListResponseDTO(
      cosponsors = List(sampleCosponsor),
      pagination = Some(PaginationInfoDTO(Some(21), Some("https://next1"))),
    )
    val cosponsor2 = sampleCosponsor.copy(bioguideId = "B000575")
    val page2 = CosponsorListResponseDTO(
      cosponsors = List(cosponsor2),
      pagination = Some(PaginationInfoDTO(Some(21), Some("https://next2"))),
    )
    val combined = page1 |+| page2
    val _        = combined.cosponsors shouldBe List(sampleCosponsor, cosponsor2)
    combined.pagination shouldBe page2.pagination
  }

  it should "decode pagination with 'next' field instead of 'url'" in {
    val json =
      """{
        |  "cosponsors": [],
        |  "pagination": {
        |    "count": 21,
        |    "next": "https://api.congress.gov/v3/bill/116/s/1/cosponsors?offset=20"
        |  }
        |}""".stripMargin

    decode[CosponsorListResponseDTO](json).map(_.pagination.flatMap(_.url)) shouldBe Right(
      Some("https://api.congress.gov/v3/bill/116/s/1/cosponsors?offset=20")
    )
  }

}
