package repcheck.shared.models.congress.dto.common

import io.circe.parser.{decode, decodeAccumulating}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SharedDTOsSpec extends AnyFlatSpec with Matchers {

  "PaginationInfoDTO" should "round-trip with all fields" in {
    val dto     = PaginationInfoDTO(count = Some(42), url = Some("https://api.congress.gov/next"))
    val json    = dto.asJson
    val decoded = json.as[PaginationInfoDTO]
    decoded shouldBe Right(dto)
  }

  it should "decode with missing optional fields" in {
    val json   = """{}"""
    val result = decode[PaginationInfoDTO](json)
    result shouldBe Right(PaginationInfoDTO(count = None, url = None))
  }

  it should "round-trip with only count" in {
    val dto = PaginationInfoDTO(count = Some(10), url = None)
    dto.asJson.as[PaginationInfoDTO] shouldBe Right(dto)
  }

  it should "round-trip with only url" in {
    val dto = PaginationInfoDTO(count = None, url = Some("https://api.congress.gov/next"))
    dto.asJson.as[PaginationInfoDTO] shouldBe Right(dto)
  }

  it should "round-trip with both fields None" in {
    val dto = PaginationInfoDTO(count = None, url = None)
    dto.asJson.as[PaginationInfoDTO] shouldBe Right(dto)
  }

  "ApiListResponseDTO" should "round-trip with items and pagination" in {
    val dto = ApiListResponseDTO(
      items = List(PaginationInfoDTO(Some(1), Some("url1")), PaginationInfoDTO(Some(2), Some("url2"))),
      pagination = Some(PaginationInfoDTO(count = Some(100), url = Some("https://next"))),
    )
    val json    = dto.asJson
    val decoded = json.as[ApiListResponseDTO[PaginationInfoDTO]]
    decoded shouldBe Right(dto)
  }

  it should "decode with empty items list" in {
    val json   = """{"items": [], "pagination": null}"""
    val result = decode[ApiListResponseDTO[PaginationInfoDTO]](json)
    result shouldBe Right(ApiListResponseDTO(items = List.empty, pagination = None))
  }

  it should "decode without pagination" in {
    val json   = """{"items": []}"""
    val result = decode[ApiListResponseDTO[PaginationInfoDTO]](json)
    result shouldBe Right(ApiListResponseDTO(items = List.empty, pagination = None))
  }

  it should "encode without pagination field when pagination is None" in {
    val dto  = ApiListResponseDTO[PaginationInfoDTO](items = List.empty, pagination = None)
    val json = dto.asJson
    json.asObject.fold(fail("expected JSON object")) { obj =>
      obj.contains("pagination") shouldBe false
      obj.contains("items") shouldBe true
    }
  }

  it should "encode with pagination field when pagination is Some" in {
    val pagination = PaginationInfoDTO(Some(50), Some("https://next"))
    val dto        = ApiListResponseDTO[PaginationInfoDTO](items = List.empty, pagination = Some(pagination))
    val json       = dto.asJson
    json.hcursor.downField("pagination").as[PaginationInfoDTO] shouldBe Right(pagination)
  }

  it should "round-trip with None pagination" in {
    val dto = ApiListResponseDTO[PaginationInfoDTO](
      items = List(PaginationInfoDTO(Some(1), None)),
      pagination = None,
    )
    dto.asJson.as[ApiListResponseDTO[PaginationInfoDTO]] shouldBe Right(dto)
  }

  "PaginationInfoDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[PaginationInfoDTO]("""{"count":10,"url":"https://example.com"}""").isValid shouldBe true
  }

  "ApiListResponseDTO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[ApiListResponseDTO[PaginationInfoDTO]]("""{"items":[]}""").isValid shouldBe true
  }

}
