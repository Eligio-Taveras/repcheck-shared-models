package repcheck.shared.models.congress.dos.vote

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class VotePositionDOSpec extends AnyFlatSpec with Matchers {

  private val samplePosition = VotePositionDO(
    voteId = "rv118-house-123",
    memberId = "M000303",
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
      voteId = "rv118-senate-456",
      memberId = "S000148",
      position = None,
      partyAtVote = None,
      stateAtVote = None,
      createdAt = None,
    )
    minimal.asJson.as[VotePositionDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field voteId" in {
    val json = """{"memberId":"M000303"}"""
    decode[VotePositionDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field memberId" in {
    val json = """{"voteId":"rv118-house-123"}"""
    decode[VotePositionDO](json).isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[VotePositionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[VotePositionDO]].shouldBe(a[AnyRef])
  }

}
