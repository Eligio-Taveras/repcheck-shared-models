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
    createdAt = Some(Instant.parse("2024-06-15T08:30:00Z")),
  )

  "LisMemberDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sample.asJson
    val decoded = json.as[LisMemberDO]
    decoded shouldBe Right(sample)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = LisMemberDO(id = 2L, naturalKey = "S002", createdAt = None)
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
