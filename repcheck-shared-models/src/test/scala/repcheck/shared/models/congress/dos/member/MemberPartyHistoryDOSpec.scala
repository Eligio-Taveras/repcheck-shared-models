package repcheck.shared.models.congress.dos.member

import java.util.UUID

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MemberPartyHistoryDOSpec extends AnyFlatSpec with Matchers {

  private val sampleHistoryId = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901")

  private val sampleHistory = MemberPartyHistoryDO(
    partyHistoryId = sampleHistoryId,
    memberId = 1L,
    partyName = Some("Democratic"),
    partyAbbreviation = Some("D"),
    startYear = Some(2001),
  )

  "MemberPartyHistoryDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleHistory.asJson
    val decoded = json.as[MemberPartyHistoryDO]
    decoded shouldBe Right(sampleHistory)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = MemberPartyHistoryDO(
      partyHistoryId = sampleHistoryId,
      memberId = 2L,
      partyName = None,
      partyAbbreviation = None,
      startYear = None,
    )
    minimal.asJson.as[MemberPartyHistoryDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[MemberPartyHistoryDO]("""{"partyHistoryId":"b2c3d4e5-f6a7-8901-bcde-f12345678901"}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Read[MemberPartyHistoryDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Write[MemberPartyHistoryDO]].shouldBe(a[AnyRef])
  }

}
