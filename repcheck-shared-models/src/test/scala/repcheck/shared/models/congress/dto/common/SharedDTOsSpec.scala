package repcheck.shared.models.congress.dto.common

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SharedDTOsSpec extends AnyFlatSpec with Matchers {

  "PaginationInfoDTO" should "round-trip with all fields" in {
    val dto = PaginationInfoDTO(count = Some(42), url = Some("https://api.congress.gov/next"))
    val json = dto.asJson
    val decoded = json.as[PaginationInfoDTO]
    decoded shouldBe Right(dto)
  }

  it should "decode with missing optional fields" in {
    val json = """{}"""
    val result = decode[PaginationInfoDTO](json)
    result shouldBe Right(PaginationInfoDTO(count = None, url = None))
  }

  "ApiListResponseDTO" should "round-trip with items and pagination" in {
    val dto = ApiListResponseDTO(
      items = List(PaginationInfoDTO(Some(1), Some("url1")), PaginationInfoDTO(Some(2), Some("url2"))),
      pagination = Some(PaginationInfoDTO(count = Some(100), url = Some("https://next")))
    )
    val json = dto.asJson
    val decoded = json.as[ApiListResponseDTO[PaginationInfoDTO]]
    decoded shouldBe Right(dto)
  }

  it should "decode with empty items list" in {
    val json = """{"items": [], "pagination": null}"""
    val result = decode[ApiListResponseDTO[PaginationInfoDTO]](json)
    result shouldBe Right(ApiListResponseDTO(items = List.empty, pagination = None))
  }

  it should "decode without pagination" in {
    val json = """{"items": []}"""
    val result = decode[ApiListResponseDTO[PaginationInfoDTO]](json)
    result shouldBe Right(ApiListResponseDTO(items = List.empty, pagination = None))
  }
}
