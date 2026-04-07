package repcheck.shared.models.congress.dto.amendment

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.bill.{LatestActionDTO, SponsorDTO}

class AmendmentDTOsSpec extends AnyFlatSpec with Matchers {

  "AmendedBillDTO" should "round-trip" in {
    val dto = AmendedBillDTO(
      congress = Some(118),
      number = Some(1234),
      originChamber = Some("House"),
      originChamberCode = Some("H"),
      title = Some("A bill to do something"),
      billType = Some("hr"),
      url = Some("https://api.congress.gov/v3/bill/118/hr/1234"),
      updateDateIncludingText = Some("2024-03-15T10:00:00Z"),
    )
    decode[AmendedBillDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with all None fields" in {
    decode[AmendedBillDTO]("{}") shouldBe Right(AmendedBillDTO(None, None, None, None, None, None, None, None))
  }

  "AmendmentListItemDTO" should "round-trip" in {
    val dto = AmendmentListItemDTO(
      congress = 118,
      number = "100",
      amendmentType = Some("HAMDT"),
      description = Some("An amendment to HR 1234"),
      latestAction = Some(LatestActionDTO("2024-03-01", "Amendment agreed to")),
      updateDate = Some("2024-03-02"),
      url = Some("https://api.congress.gov/v3/amendment/118/hamdt/100"),
    )
    decode[AmendmentListItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with only required fields" in {
    val json   = """{"congress":118,"number":"50"}"""
    val result = decode[AmendmentListItemDTO](json)
    val _      = result.isRight shouldBe true
    result.map(_.amendmentType) shouldBe Right(None)
  }

  "AmendmentDetailDTO" should "round-trip with all fields" in {
    val dto = AmendmentDetailDTO(
      congress = 118,
      number = "200",
      amendmentType = Some("SAMDT"),
      amendedBill = Some(
        AmendedBillDTO(Some(118), Some(5678), Some("Senate"), Some("S"), Some("Test Bill"), Some("s"), None, None)
      ),
      chamber = Some("Senate"),
      description = Some("Amendment description"),
      purpose = Some("To improve the bill"),
      sponsors = Some(
        List(SponsorDTO("S000033", Some("Bernard"), Some("Sanders"), None, None, None, Some("I"), Some("VT"), None))
      ),
      submittedDate = Some("2024-02-15"),
      latestAction = Some(LatestActionDTO("2024-03-01", "Submitted")),
      updateDate = Some("2024-03-15"),
      actions = Some(List(LatestActionDTO("2024-03-01", "Submitted"), LatestActionDTO("2024-03-10", "Agreed to"))),
      textVersions = Some(List("v1", "v2")),
    )
    decode[AmendmentDetailDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with only required fields" in {
    val json   = """{"congress":118,"number":"300"}"""
    val result = decode[AmendmentDetailDTO](json)
    val _      = result.isRight shouldBe true
    val _      = result.map(_.amendedBill) shouldBe Right(None)
    val _      = result.map(_.sponsors) shouldBe Right(None)
    val _      = result.map(_.actions) shouldBe Right(None)
    result.map(_.textVersions) shouldBe Right(None)
  }

  "AmendmentListResponseDTO" should "round-trip via Circe" in {
    import repcheck.shared.models.congress.dto.common.PaginationInfoDTO
    val item = AmendmentListItemDTO(118, "100", None, None, None, None, None)
    val resp = AmendmentListResponseDTO(List(item), Some(PaginationInfoDTO(Some(1), None)))
    decode[AmendmentListResponseDTO](resp.asJson.noSpaces) shouldBe Right(resp)
  }

  it should "combine via Semigroup" in {
    import cats.Semigroup
    import repcheck.shared.models.congress.dto.common.PaginationInfoDTO
    val a = AmendmentListResponseDTO(
      List(AmendmentListItemDTO(118, "1", None, None, None, None, None)),
      Some(PaginationInfoDTO(Some(1), Some("p1"))),
    )
    val b = AmendmentListResponseDTO(
      List(AmendmentListItemDTO(118, "2", None, None, None, None, None)),
      Some(PaginationInfoDTO(Some(1), Some("p2"))),
    )
    val combined = Semigroup[AmendmentListResponseDTO].combine(a, b)
    val _        = combined.items should have size 2
    combined.pagination shouldBe b.pagination
  }

}
