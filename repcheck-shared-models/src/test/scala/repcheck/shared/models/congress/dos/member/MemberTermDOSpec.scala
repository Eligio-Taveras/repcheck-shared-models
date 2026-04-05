package repcheck.shared.models.congress.dos.member

import java.util.UUID

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MemberTermDOSpec extends AnyFlatSpec with Matchers {

  private val sampleTermId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")

  private val sampleTerm = MemberTermDO(
    termId = sampleTermId,
    memberId = "B001234",
    chamber = Some("House"),
    congress = Some(118),
    startYear = Some(2023),
    endYear = Some(2025),
    memberType = Some("Representative"),
    stateCode = Some("CA"),
    stateName = Some("California"),
    district = Some(12),
  )

  "MemberTermDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleTerm.asJson
    val decoded = json.as[MemberTermDO]
    decoded shouldBe Right(sampleTerm)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = MemberTermDO(
      termId = sampleTermId,
      memberId = "M000999",
      chamber = None,
      congress = None,
      startYear = None,
      endYear = None,
      memberType = None,
      stateCode = None,
      stateName = None,
      district = None,
    )
    minimal.asJson.as[MemberTermDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[MemberTermDO]("""{"termId":"a1b2c3d4-e5f6-7890-abcd-ef1234567890"}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[MemberTermDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[MemberTermDO]].shouldBe(a[AnyRef])
  }

}
