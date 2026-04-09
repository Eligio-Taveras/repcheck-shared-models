package repcheck.shared.models.congress.dos.member

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MemberPartyHistoryDOSpec extends AnyFlatSpec with Matchers {

  private val sampleHistory = MemberPartyHistoryDO(
    id = 1L,
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
      id = 2L,
      memberId = 2L,
      partyName = None,
      partyAbbreviation = None,
      startYear = None,
    )
    minimal.asJson.as[MemberPartyHistoryDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[MemberPartyHistoryDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    implicitly[Read[MemberPartyHistoryDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    implicitly[Write[MemberPartyHistoryDO]].shouldBe(a[AnyRef])
  }

}
