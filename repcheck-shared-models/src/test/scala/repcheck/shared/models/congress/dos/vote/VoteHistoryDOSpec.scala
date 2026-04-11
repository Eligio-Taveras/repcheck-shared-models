package repcheck.shared.models.congress.dos.vote

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.{Chamber, Party}
import repcheck.shared.models.congress.vote.VoteCast

class VoteHistoryDOSpec extends AnyFlatSpec with Matchers {

  private val sampleHistory = VoteHistoryDO(
    id = 1L,
    voteId = 1L,
    congress = 118,
    chamber = Chamber.House,
    rollNumber = 123,
    sessionNumber = Some(1),
    billId = Some(1L),
    question = Some("On Passage"),
    voteType = Some("YEA-AND-NAY"),
    voteMethod = Some("recorded vote"),
    result = Some("Passed"),
    voteDate = Some("2024-03-15"),
    legislationNumber = Some("H.R. 1234"),
    legislationType = Some("HR"),
    legislationUrl = Some("https://congress.gov/bill/118/hr/1234"),
    sourceDataUrl = Some("https://clerk.house.gov/evs/2024/roll123.xml"),
    updateDate = Some("2024-03-16T10:00:00Z"),
    archivedAt = Some(Instant.parse("2024-03-16T12:00:00Z")),
  )

  private val sampleHistoryPosition = VoteHistoryPositionDO(
    historyId = 1L,
    memberId = 1L,
    position = Some(VoteCast.Yea),
    partyAtVote = Some(Party.Democrat),
    stateAtVote = Some("NY"),
  )

  // --- VoteHistoryDO ---

  "VoteHistoryDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleHistory.asJson
    val decoded = json.as[VoteHistoryDO]
    decoded shouldBe Right(sampleHistory)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = VoteHistoryDO(
      id = 2L,
      voteId = 2L,
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
      archivedAt = None,
    )
    minimal.asJson.as[VoteHistoryDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field id" in {
    val json = """{"voteId":1,"congress":118,"chamber":"House","rollNumber":1}"""
    decode[VoteHistoryDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field voteId" in {
    val json = """{"id":1,"congress":118,"chamber":"House","rollNumber":1}"""
    decode[VoteHistoryDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field congress" in {
    val json = """{"id":1,"voteId":1,"chamber":"House","rollNumber":1}"""
    decode[VoteHistoryDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field chamber" in {
    val json = """{"id":1,"voteId":1,"congress":118,"rollNumber":1}"""
    decode[VoteHistoryDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field rollNumber" in {
    val json = """{"id":1,"voteId":1,"congress":118,"chamber":"House"}"""
    decode[VoteHistoryDO](json).isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Read[VoteHistoryDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Write[VoteHistoryDO]].shouldBe(a[AnyRef])
  }

  // --- VoteHistoryPositionDO ---

  "VoteHistoryPositionDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleHistoryPosition.asJson
    val decoded = json.as[VoteHistoryPositionDO]
    decoded shouldBe Right(sampleHistoryPosition)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = VoteHistoryPositionDO(
      historyId = 2L,
      memberId = 2L,
      position = None,
      partyAtVote = None,
      stateAtVote = None,
    )
    minimal.asJson.as[VoteHistoryPositionDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field historyId" in {
    val json = """{"memberId":1}"""
    decode[VoteHistoryPositionDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field memberId" in {
    val json = """{"historyId":1}"""
    decode[VoteHistoryPositionDO](json).isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Read[VoteHistoryPositionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Write[VoteHistoryPositionDO]].shouldBe(a[AnyRef])
  }

}
