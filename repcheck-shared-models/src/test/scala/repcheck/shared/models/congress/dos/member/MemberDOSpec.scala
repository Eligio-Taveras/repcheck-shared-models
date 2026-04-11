package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.{Party, UsState}

class MemberDOSpec extends AnyFlatSpec with Matchers {

  private val sampleMember = MemberDO(
    memberId = 1L,
    naturalKey = "B001234",
    firstName = Some("John"),
    lastName = Some("Smith"),
    directOrderName = Some("John Smith"),
    invertedOrderName = Some("Smith, John"),
    honorificName = Some("Rep."),
    birthYear = Some(1965),
    currentParty = Some(Party.Democrat),
    state = Some(UsState.California),
    district = Some(12),
    imageUrl = Some("https://bioguide.congress.gov/photo/B/B001234.jpg"),
    imageAttribution = Some("Courtesy U.S. Congress"),
    officialUrl = Some("https://smith.house.gov"),
    updateDate = Some(Instant.parse("2024-03-01T12:00:00Z")),
    createdAt = Some(Instant.parse("2024-01-15T10:30:00Z")),
    updatedAt = Some(Instant.parse("2024-06-01T14:00:00Z")),
  )

  "MemberDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleMember.asJson
    val decoded = json.as[MemberDO]
    decoded shouldBe Right(sampleMember)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = MemberDO(
      memberId = 2L,
      naturalKey = "M000999",
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
      createdAt = None,
      updatedAt = None,
    )
    minimal.asJson.as[MemberDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[MemberDO]("""{}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Read[MemberDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Write[MemberDO]].shouldBe(a[AnyRef])
  }

  "Member DO package" should "accumulate decode errors for MemberDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[MemberDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for MemberTermDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[MemberTermDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for MemberPartyHistoryDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[MemberPartyHistoryDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for LisMemberDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[LisMemberDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for MemberLisMappingDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[MemberLisMappingDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for MemberHistoryDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[MemberHistoryDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for MemberTermHistoryDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[MemberTermHistoryDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
