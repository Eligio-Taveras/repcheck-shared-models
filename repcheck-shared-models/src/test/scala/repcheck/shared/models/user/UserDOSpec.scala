package repcheck.shared.models.user

import java.time.Instant
import java.util.UUID

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UserDOSpec extends AnyFlatSpec with Matchers {

  private val sampleUser = UserDO(
    userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
    displayName = Some("Jane Doe"),
    email = Some("jane@example.com"),
    state = Some("NY"),
    district = Some(14),
    createdAt = Some(Instant.parse("2024-01-15T10:30:00Z")),
    updatedAt = Some(Instant.parse("2024-06-01T14:00:00Z")),
  )

  "UserDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleUser.asJson
    val decoded = json.as[UserDO]
    decoded shouldBe Right(sampleUser)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = UserDO(
      userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
      displayName = None,
      email = None,
      state = None,
      district = None,
      createdAt = None,
      updatedAt = None,
    )
    minimal.asJson.as[UserDO] shouldBe Right(minimal)
  }

  it should "fail on missing userId" in {
    decode[UserDO]("""{"displayName":"Test"}""").isLeft shouldBe true
  }

}
