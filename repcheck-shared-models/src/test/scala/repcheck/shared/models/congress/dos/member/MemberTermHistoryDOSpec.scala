package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.{Chamber, UsState}
import repcheck.shared.models.congress.member.MemberType

class MemberTermHistoryDOSpec extends AnyFlatSpec with Matchers {

  private val sampleHistory = MemberTermHistoryDO(
    historyId = 1L,
    memberId = 1L,
    chamber = Some(Chamber.House),
    congress = Some(118),
    startYear = Some(2023),
    endYear = Some(2025),
    memberType = Some(MemberType.Representative),
    stateCode = Some(UsState.California),
    stateName = Some("California"),
    district = Some(12),
    archivedAt = Some(Instant.parse("2024-06-01T14:00:00Z")),
  )

  "MemberTermHistoryDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleHistory.asJson
    val decoded = json.as[MemberTermHistoryDO]
    decoded shouldBe Right(sampleHistory)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = MemberTermHistoryDO(
      historyId = 2L,
      memberId = 2L,
      chamber = None,
      congress = None,
      startYear = None,
      endYear = None,
      memberType = None,
      stateCode = None,
      stateName = None,
      district = None,
      archivedAt = None,
    )
    minimal.asJson.as[MemberTermHistoryDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field historyId" in {
    val json = """{"memberId":1}"""
    decode[MemberTermHistoryDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field memberId" in {
    val json = """{"historyId":1}"""
    decode[MemberTermHistoryDO](json).isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Read[MemberTermHistoryDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Write[MemberTermHistoryDO]].shouldBe(a[AnyRef])
  }

  "MemberTermHistoryDO decodeAccumulating" should "accumulate multiple errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[MemberTermHistoryDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
