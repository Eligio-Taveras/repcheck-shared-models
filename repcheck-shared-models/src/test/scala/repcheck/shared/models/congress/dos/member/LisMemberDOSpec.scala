package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LisMemberDOSpec extends AnyFlatSpec with Matchers {

  private val sample = LisMemberDO(
    id = 1L,
    naturalKey = "S001",
    firstName = Some("John"),
    lastName = Some("Smith"),
    party = Some("Democratic"),
    state = Some("NY"),
    lastVerified = Some(Instant.parse("2024-06-15T10:00:00Z")),
    createdAt = Some(Instant.parse("2024-06-15T08:30:00Z")),
  )

  "LisMemberDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sample.asJson
    val decoded = json.as[LisMemberDO]
    decoded shouldBe Right(sample)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = LisMemberDO(
      id = 2L,
      naturalKey = "S002",
      firstName = None,
      lastName = None,
      party = None,
      state = None,
      lastVerified = None,
      createdAt = None,
    )
    minimal.asJson.as[LisMemberDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field id" in {
    val json = """{"naturalKey":"S001"}"""
    decode[LisMemberDO](json).isLeft shouldBe true
  }

  it should "fail on missing required field naturalKey" in {
    val json = """{"id":1}"""
    decode[LisMemberDO](json).isLeft shouldBe true
  }

  it should "encode new fields in JSON" in {
    val json = sample.asJson
    val _    = json.hcursor.get[String]("firstName") shouldBe Right("John")
    val _    = json.hcursor.get[String]("lastName") shouldBe Right("Smith")
    val _    = json.hcursor.get[String]("party") shouldBe Right("Democratic")
    val _    = json.hcursor.get[String]("state") shouldBe Right("NY")
    json.hcursor.get[String]("lastVerified") shouldBe Right("2024-06-15T10:00:00Z")
  }

  it should "decode with only required fields and omit optional ones" in {
    val json   = """{"id":3,"naturalKey":"H001"}"""
    val result = decode[LisMemberDO](json)
    result.fold(
      err => fail(s"Expected successful decode but got: $err"),
      decoded => {
        val _ = decoded.id shouldBe 3L
        val _ = decoded.naturalKey shouldBe "H001"
        val _ = decoded.firstName shouldBe None
        val _ = decoded.lastName shouldBe None
        val _ = decoded.party shouldBe None
        val _ = decoded.state shouldBe None
        val _ = decoded.lastVerified shouldBe None
        decoded.createdAt shouldBe None
      },
    )
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Read[LisMemberDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Write[LisMemberDO]].shouldBe(a[AnyRef])
  }

}
