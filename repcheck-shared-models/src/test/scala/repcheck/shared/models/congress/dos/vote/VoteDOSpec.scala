package repcheck.shared.models.congress.dos.vote

import java.time.{Instant, LocalDate}

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.{BillType, Chamber}
import repcheck.shared.models.congress.vote.VoteMethod

class VoteDOSpec extends AnyFlatSpec with Matchers {

  private val sampleVote = VoteDO(
    voteId = 1L,
    naturalKey = "rv118-house-123",
    congress = 118,
    chamber = Chamber.House,
    rollNumber = 123,
    sessionNumber = Some(1),
    billId = Some(1L),
    question = Some("On Passage"),
    voteType = Some("YEA-AND-NAY"),
    voteMethod = Some(VoteMethod.RecordedVote),
    result = Some("Passed"),
    voteDate = Some(LocalDate.parse("2024-03-15")),
    legislationNumber = Some("H.R. 1234"),
    legislationType = Some(BillType.HR),
    legislationUrl = Some("https://congress.gov/bill/118/hr/1234"),
    sourceDataUrl = Some("https://clerk.house.gov/evs/2024/roll123.xml"),
    updateDate = Some(Instant.parse("2024-03-16T10:00:00Z")),
    createdAt = Some(Instant.parse("2024-03-15T10:00:00Z")),
    updatedAt = Some(Instant.parse("2024-03-16T12:00:00Z")),
  )

  "VoteDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleVote.asJson
    val decoded = json.as[VoteDO]
    decoded shouldBe Right(sampleVote)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = VoteDO(
      voteId = 2L,
      naturalKey = "rv118-senate-456",
      congress = 118,
      chamber = Chamber.Senate,
      rollNumber = 456,
      sessionNumber = None,
      billId = None,
      question = None,
      voteType = None,
      voteMethod = None,
      result = None,
      voteDate = None,
      legislationNumber = None,
      legislationType = None,
      legislationUrl = None,
      sourceDataUrl = None,
      updateDate = None,
      createdAt = None,
      updatedAt = None,
    )
    minimal.asJson.as[VoteDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field voteId" in {
    val json = """{"naturalKey":"rv118-house-1","congress":118,"chamber":"House","rollNumber":123}"""
    decode[VoteDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field naturalKey" in {
    val json = """{"voteId":1,"congress":118,"chamber":"House","rollNumber":1}"""
    decode[VoteDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field congress" in {
    val json = """{"voteId":1,"naturalKey":"rv118-house-1","chamber":"House","rollNumber":1}"""
    decode[VoteDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field chamber" in {
    val json = """{"voteId":1,"naturalKey":"rv118-house-1","congress":118,"rollNumber":1}"""
    decode[VoteDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field rollNumber" in {
    val json = """{"voteId":1,"naturalKey":"rv118-house-1","congress":118,"chamber":"House"}"""
    decode[VoteDO](json).isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Read[VoteDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Write[VoteDO]].shouldBe(a[AnyRef])
  }

  "Vote DO package" should "accumulate decode errors for VoteDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[VoteDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for VotePositionDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[VotePositionDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for VoteHistoryDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[VoteHistoryDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for VoteHistoryPositionDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[VoteHistoryPositionDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
