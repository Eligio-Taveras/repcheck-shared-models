package repcheck.shared.models.congress.dos.member

import java.time.Instant
import java.util.UUID

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MemberHistoryDOSpec extends AnyFlatSpec with Matchers {

  private val historyId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

  private val sampleHistory = MemberHistoryDO(
    historyId = historyId,
    memberId = 1L,
    firstName = Some("John"),
    lastName = Some("Smith"),
    directOrderName = Some("John Smith"),
    invertedOrderName = Some("Smith, John"),
    honorificName = Some("Rep."),
    birthYear = Some("1965"),
    currentParty = Some("Democratic"),
    state = Some("California"),
    district = Some(12),
    imageUrl = Some("https://bioguide.congress.gov/photo/B/B001234.jpg"),
    imageAttribution = Some("Courtesy U.S. Congress"),
    officialUrl = Some("https://smith.house.gov"),
    updateDate = Some("2024-03-01T12:00:00Z"),
    archivedAt = Some(Instant.parse("2024-06-01T14:00:00Z")),
  )

  "MemberHistoryDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleHistory.asJson
    val decoded = json.as[MemberHistoryDO]
    decoded shouldBe Right(sampleHistory)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = MemberHistoryDO(
      historyId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
      memberId = 2L,
      firstName = None,
      lastName = None,
      directOrderName = None,
      invertedOrderName = None,
      honorificName = None,
      birthYear = None,
      currentParty = None,
      state = None,
      district = None,
      imageUrl = None,
      imageAttribution = None,
      officialUrl = None,
      updateDate = None,
      archivedAt = None,
    )
    minimal.asJson.as[MemberHistoryDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field historyId" in {
    val json = """{"memberId":1}"""
    decode[MemberHistoryDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field memberId" in {
    val json = """{"historyId":"550e8400-e29b-41d4-a716-446655440000"}"""
    decode[MemberHistoryDO](json).isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Read[MemberHistoryDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Write[MemberHistoryDO]].shouldBe(a[AnyRef])
  }

  "MemberHistoryDO decodeAccumulating" should "accumulate multiple errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[MemberHistoryDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
