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
      url = Some("https://api.congress.gov/v3/bill/118/hr/1234")
    )
    decode[AmendedBillDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with all None fields" in {
    decode[AmendedBillDTO]("{}") shouldBe Right(AmendedBillDTO(None, None, None, None, None, None, None))
  }

  "AmendmentListItemDTO" should "round-trip" in {
    val dto = AmendmentListItemDTO(
      congress = 118,
      number = "100",
      amendmentType = Some("HAMDT"),
      description = Some("An amendment to HR 1234"),
      latestAction = Some(LatestActionDTO("2024-03-01", "Amendment agreed to")),
      updateDate = Some("2024-03-02"),
      url = Some("https://api.congress.gov/v3/amendment/118/hamdt/100")
    )
    decode[AmendmentListItemDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with only required fields" in {
    val json = """{"congress":118,"number":"50"}"""
    val result = decode[AmendmentListItemDTO](json)
    result.isRight shouldBe true
    result.map(_.amendmentType) shouldBe Right(None)
  }

  "AmendmentDetailDTO" should "round-trip with all fields" in {
    val dto = AmendmentDetailDTO(
      congress = 118,
      number = "200",
      amendmentType = Some("SAMDT"),
      amendedBill = Some(AmendedBillDTO(Some(118), Some(5678), Some("Senate"), Some("S"), Some("Test Bill"), Some("s"), None)),
      chamber = Some("Senate"),
      description = Some("Amendment description"),
      purpose = Some("To improve the bill"),
      sponsors = Some(List(SponsorDTO("S000033", Some("Bernard"), Some("Sanders"), None, None, None, Some("I"), Some("VT"), None))),
      submittedDate = Some("2024-02-15"),
      latestAction = Some(LatestActionDTO("2024-03-01", "Submitted")),
      updateDate = Some("2024-03-15")
    )
    decode[AmendmentDetailDTO](dto.asJson.noSpaces) shouldBe Right(dto)
  }

  it should "decode with only required fields" in {
    val json = """{"congress":118,"number":"300"}"""
    val result = decode[AmendmentDetailDTO](json)
    result.isRight shouldBe true
    result.map(_.amendedBill) shouldBe Right(None)
    result.map(_.sponsors) shouldBe Right(None)
  }
}
