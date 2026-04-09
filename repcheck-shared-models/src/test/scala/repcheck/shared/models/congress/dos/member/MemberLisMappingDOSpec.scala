package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MemberLisMappingDOSpec extends AnyFlatSpec with Matchers {

  private val sampleMapping = MemberLisMappingDO(
    id = 1L,
    memberId = 1L,
    lisMemberId = 10L,
    lastVerified = Instant.parse("2024-06-15T08:30:00Z"),
  )

  "MemberLisMappingDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleMapping.asJson
    val decoded = json.as[MemberLisMappingDO]
    decoded shouldBe Right(sampleMapping)
  }

  it should "fail on missing required field id" in {
    val json = """{"memberId":1,"lisMemberId":10,"lastVerified":"2024-06-15T08:30:00Z"}"""
    decode[MemberLisMappingDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field memberId" in {
    val json = """{"id":1,"lisMemberId":10,"lastVerified":"2024-06-15T08:30:00Z"}"""
    decode[MemberLisMappingDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field lisMemberId" in {
    val json = """{"id":1,"memberId":1,"lastVerified":"2024-06-15T08:30:00Z"}"""
    decode[MemberLisMappingDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field lastVerified" in {
    val json = """{"id":1,"memberId":1,"lisMemberId":10}"""
    decode[MemberLisMappingDO](json).isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Read[MemberLisMappingDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Write[MemberLisMappingDO]].shouldBe(a[AnyRef])
  }

}
