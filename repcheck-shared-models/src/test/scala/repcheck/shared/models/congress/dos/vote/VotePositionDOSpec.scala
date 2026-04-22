package repcheck.shared.models.congress.dos.vote

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.{Party, UsState}
import repcheck.shared.models.congress.vote.VoteCast

class VotePositionDOSpec extends AnyFlatSpec with Matchers {

  private val houseArm = VotePositionDO(
    id = 10L,
    voteId = 1L,
    memberId = Some(42L),
    position = Some(VoteCast.Yea),
    partyAtVote = Some(Party.Democrat),
    stateAtVote = Some(UsState.NewYork),
    createdAt = Some(Instant.parse("2024-03-15T10:00:00Z")),
    lisMemberId = None,
  )

  private val senateArm = VotePositionDO(
    id = 11L,
    voteId = 2L,
    memberId = None,
    position = Some(VoteCast.Nay),
    partyAtVote = Some(Party.Republican),
    stateAtVote = Some(UsState.Texas),
    createdAt = Some(Instant.parse("2024-03-16T11:00:00Z")),
    lisMemberId = Some(9001L),
  )

  "VotePositionDO Circe codec" should "round-trip the House arm (memberId populated, lisMemberId None)" in {
    val json    = houseArm.asJson
    val decoded = json.as[VotePositionDO]
    decoded shouldBe Right(houseArm)
  }

  it should "round-trip the Senate arm (lisMemberId populated, memberId None)" in {
    val json    = senateArm.asJson
    val decoded = json.as[VotePositionDO]
    decoded shouldBe Right(senateArm)
  }

  it should "round-trip with all optional fields as None (both identity arms empty — DB CHECK would reject this)" in {
    val minimal = VotePositionDO(
      id = 3L,
      voteId = 3L,
      memberId = None,
      position = None,
      partyAtVote = None,
      stateAtVote = None,
      createdAt = None,
      lisMemberId = None,
    )
    minimal.asJson.as[VotePositionDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field id" in {
    val json = """{"voteId":1}"""
    decode[VotePositionDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field voteId" in {
    val json = """{"id":1}"""
    decode[VotePositionDO](json).isLeft shouldBe true
  }

  it should "encode memberId and lisMemberId as nullable JSON fields" in {
    val houseJson = houseArm.asJson
    val _         = houseJson.hcursor.get[Long]("memberId") shouldBe Right(42L)
    val _         = houseJson.hcursor.get[Option[Long]]("lisMemberId") shouldBe Right(None)

    val senateJson = senateArm.asJson
    val _          = senateJson.hcursor.get[Option[Long]]("memberId") shouldBe Right(None)
    senateJson.hcursor.get[Long]("lisMemberId") shouldBe Right(9001L)
  }

  it should "decode with only required fields and omit optional ones" in {
    val json   = """{"id":5,"voteId":7}"""
    val result = decode[VotePositionDO](json)
    result.fold(
      err => fail(s"Expected successful decode but got: $err"),
      decoded => {
        val _ = decoded.id shouldBe 5L
        val _ = decoded.voteId shouldBe 7L
        val _ = decoded.memberId shouldBe None
        val _ = decoded.lisMemberId shouldBe None
        val _ = decoded.position shouldBe None
        val _ = decoded.partyAtVote shouldBe None
        val _ = decoded.stateAtVote shouldBe None
        decoded.createdAt shouldBe None
      },
    )
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Read[VotePositionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Write[VotePositionDO]].shouldBe(a[AnyRef])
  }

}
