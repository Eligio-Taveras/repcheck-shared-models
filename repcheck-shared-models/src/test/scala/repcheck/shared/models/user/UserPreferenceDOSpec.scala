package repcheck.shared.models.user

import java.time.Instant
import java.util.UUID

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UserPreferenceDOSpec extends AnyFlatSpec with Matchers {

  private val samplePref = UserPreferenceDO(
    id = 1L,
    userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
    topic = "Healthcare",
    stance = "support universal coverage",
    importance = 8,
    embedding = Some(Array(0.1f, 0.2f, 0.3f)),
    updatedAt = Some(Instant.parse("2024-06-01T14:00:00Z")),
  )

  "UserPreferenceDO Circe codec" should "round-trip with all fields" in {
    val decoded = samplePref.asJson.as[UserPreferenceDO]
    val _       = decoded.isRight shouldBe true
    decoded.foreach { result =>
      val _ = result.id shouldBe samplePref.id
      val _ = result.topic shouldBe samplePref.topic
      val _ = result.importance shouldBe samplePref.importance
      result.embedding.map(_.toSeq) shouldBe samplePref.embedding.map(_.toSeq)
    }
  }

  it should "round-trip with embedding as None" in {
    val noEmbed = samplePref.copy(embedding = None)
    val decoded = noEmbed.asJson.as[UserPreferenceDO]
    val _       = decoded.isRight shouldBe true
    decoded.foreach(result => result.embedding shouldBe None)
  }

  "importance" should "accept values 1-10 at the model level (DB-enforced constraint)" in {
    val _ = samplePref.copy(importance = 1).importance shouldBe 1
    samplePref.copy(importance = 10).importance shouldBe 10
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec._
    implicitly[Read[UserPreferenceDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec._
    implicitly[Write[UserPreferenceDO]].shouldBe(a[AnyRef])
  }

}
