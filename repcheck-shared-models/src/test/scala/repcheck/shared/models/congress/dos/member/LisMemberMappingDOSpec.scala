package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LisMemberMappingDOSpec extends AnyFlatSpec with Matchers {

  private val sampleMapping = LisMemberMappingDO(
    lisMemberId = "S001",
    memberId = 1L,
    lastVerified = Instant.parse("2024-06-15T08:30:00Z"),
  )

  "LisMemberMappingDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleMapping.asJson
    val decoded = json.as[LisMemberMappingDO]
    decoded shouldBe Right(sampleMapping)
  }

  it should "fail on missing required field lisMemberId" in {
    val json = """{"memberId":1,"lastVerified":"2024-06-15T08:30:00Z"}"""
    decode[LisMemberMappingDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field memberId" in {
    val json = """{"lisMemberId":"S001","lastVerified":"2024-06-15T08:30:00Z"}"""
    decode[LisMemberMappingDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field lastVerified" in {
    val json = """{"lisMemberId":"S001","memberId":1}"""
    decode[LisMemberMappingDO](json).isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Read[LisMemberMappingDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Write[LisMemberMappingDO]].shouldBe(a[AnyRef])
  }

}
