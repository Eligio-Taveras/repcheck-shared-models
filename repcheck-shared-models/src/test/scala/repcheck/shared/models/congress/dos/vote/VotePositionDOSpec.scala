package repcheck.shared.models.congress.dos.vote

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class VotePositionDOSpec extends AnyFlatSpec with Matchers {

  private val samplePosition = VotePositionDO(
    voteId = 1L,
    memberId = 1L,
    position = Some("Yea"),
    partyAtVote = Some("D"),
    stateAtVote = Some("NY"),
    createdAt = Some(Instant.parse("2024-03-15T10:00:00Z")),
  )

  "VotePositionDO Circe codec" should "round-trip with all fields populated" in {
    val json    = samplePosition.asJson
    val decoded = json.as[VotePositionDO]
    decoded shouldBe Right(samplePosition)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = VotePositionDO(
      voteId = 2L,
      memberId = 2L,
      position = None,
      partyAtVote = None,
      stateAtVote = None,
      createdAt = None,
    )
    minimal.asJson.as[VotePositionDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field voteId" in {
    val json = """{"memberId":1}"""
    decode[VotePositionDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field memberId" in {
    val json = """{"voteId":1}"""
    decode[VotePositionDO](json).isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Read[VotePositionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Write[VotePositionDO]].shouldBe(a[AnyRef])
  }

}
