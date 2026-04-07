package repcheck.shared.models.congress.dto.common

import cats.Semigroup

import io.circe.parser.{decode, decodeAccumulating}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.dto.bill.{BillListItemDTO, BillListResponseDTO, LatestActionDTO}

class PagedObjectSpec extends AnyFlatSpec with Matchers {

  private val item1 = BillListItemDTO(
    congress = 118,
    number = "1",
    billType = "hr",
    latestAction = Some(LatestActionDTO("2024-01-01", "Introduced")),
    originChamber = Some("House"),
    originChamberCode = Some("H"),
    title = "Test Bill 1",
    updateDate = Some("2024-01-01"),
    updateDateIncludingText = None,
    url = "https://api.congress.gov/v3/bill/118/hr/1",
  )

  private val item2 = BillListItemDTO(
    congress = 118,
    number = "2",
    billType = "s",
    latestAction = None,
    originChamber = Some("Senate"),
    originChamberCode = Some("S"),
    title = "Test Bill 2",
    updateDate = Some("2024-02-01"),
    updateDateIncludingText = Some("2024-02-02"),
    url = "https://api.congress.gov/v3/bill/118/s/2",
  )

  private val pagination1 = Some(PaginationInfoDTO(count = Some(1), url = Some("page1")))
  private val pagination2 = Some(PaginationInfoDTO(count = Some(1), url = Some("page2")))

  private val response1 = BillListResponseDTO(
    items = List(item1),
    pagination = pagination1,
  )

  private val response2 = BillListResponseDTO(
    items = List(item2),
    pagination = pagination2,
  )

  "BillListResponseDTO" should "round-trip via Circe" in {
    val json    = response1.asJson.noSpaces
    val decoded = decode[BillListResponseDTO](json)
    decoded shouldBe Right(response1)
  }

  it should "round-trip with empty items" in {
    val empty   = BillListResponseDTO(items = List.empty, pagination = None)
    val json    = empty.asJson.noSpaces
    val decoded = decode[BillListResponseDTO](json)
    decoded shouldBe Right(empty)
  }

  it should "decodeAccumulating successfully" in {
    val json   = response1.asJson.noSpaces
    val result = decodeAccumulating[BillListResponseDTO](json)
    result.isValid shouldBe true
  }

  it should "decodeAccumulating report errors for invalid JSON" in {
    val badJson = """{"items":"not-a-list"}"""
    val result  = decodeAccumulating[BillListResponseDTO](badJson)
    result.isInvalid shouldBe true
  }

  "BillListResponseDTO Semigroup" should "concatenate items and keep second pagination" in {
    val combined = Semigroup[BillListResponseDTO].combine(response1, response2)
    val _        = combined.items shouldBe List(item1, item2)
    combined.pagination shouldBe pagination2
  }

  it should "work with cats |+| syntax" in {
    import cats.syntax.semigroup._
    val combined = response1 |+| response2
    val _        = combined.items should have size 2
    combined.pagination shouldBe pagination2
  }

  "BillListResponseDTO" should "extend PagedObject" in {
    val paged: PagedObject[BillListItemDTO] = response1
    val _                                   = paged.items shouldBe List(item1)
    paged.pagination shouldBe pagination1
  }

}
