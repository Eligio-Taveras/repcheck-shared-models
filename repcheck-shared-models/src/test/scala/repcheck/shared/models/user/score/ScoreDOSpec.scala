package repcheck.shared.models.user.score

import java.time.Instant
import java.util.UUID

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScoreDOSpec extends AnyFlatSpec with Matchers {

  private val uid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

  "ScoreDO Circe codec" should "round-trip" in {
    val score = ScoreDO(uid, "M000303", 0.85f, Some(Instant.parse("2024-06-01T14:00:00Z")), Some("vote_cast"))
    score.asJson.as[ScoreDO] shouldBe Right(score)
  }

  it should "round-trip with None fields" in {
    val score = ScoreDO(uid, "M000303", 0.5f, None, None)
    score.asJson.as[ScoreDO] shouldBe Right(score)
  }

  "ScoreTopicDO Circe codec" should "round-trip" in {
    val st = ScoreTopicDO(uid, "M000303", "healthcare", 0.9f, Some("hr-1234-118"))
    st.asJson.as[ScoreTopicDO] shouldBe Right(st)
  }

  it should "round-trip with topBillId None" in {
    val st = ScoreTopicDO(uid, "M000303", "defense", 0.7f, None)
    st.asJson.as[ScoreTopicDO] shouldBe Right(st)
  }

  "ScoreCongressDO Circe codec" should "round-trip" in {
    val sc = ScoreCongressDO(uid, "M000303", 118, 0.82f, Some(42))
    sc.asJson.as[ScoreCongressDO] shouldBe Right(sc)
  }

  it should "round-trip with votesCounted None" in {
    val sc = ScoreCongressDO(uid, "M000303", 117, 0.6f, None)
    sc.asJson.as[ScoreCongressDO] shouldBe Right(sc)
  }

  "ScoreCongressTopicDO Circe codec" should "round-trip" in {
    val sct = ScoreCongressTopicDO(uid, "M000303", 118, "environment", 0.75f)
    sct.asJson.as[ScoreCongressTopicDO] shouldBe Right(sct)
  }

  "ScoreDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[ScoreDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[ScoreDO]].shouldBe(a[AnyRef])
  }

  "ScoreTopicDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[ScoreTopicDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[ScoreTopicDO]].shouldBe(a[AnyRef])
  }

  "ScoreCongressDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[ScoreCongressDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[ScoreCongressDO]].shouldBe(a[AnyRef])
  }

  "ScoreCongressTopicDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[ScoreCongressTopicDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[ScoreCongressTopicDO]].shouldBe(a[AnyRef])
  }

}
