package repcheck.shared.models.user.score

import java.time.Instant
import java.util.UUID

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScoreHistoryDOSpec extends AnyFlatSpec with Matchers {

  private val scoreId = UUID.fromString("770e8400-e29b-41d4-a716-446655440000")
  private val uid     = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

  "ScoreHistoryDO Circe codec" should "round-trip with reasoning Some" in {
    val sh = ScoreHistoryDO(scoreId, uid, "M000303",
      Some(Instant.parse("2024-06-01T14:00:00Z")), 0.85f,
      Some("vote_cast"), Some("Strong alignment on healthcare votes"))
    sh.asJson.as[ScoreHistoryDO] shouldBe Right(sh)
  }

  it should "round-trip with reasoning None" in {
    val sh = ScoreHistoryDO(scoreId, uid, "M000303", None, 0.5f, None, None)
    sh.asJson.as[ScoreHistoryDO] shouldBe Right(sh)
  }

  "ScoreHistoryCongressDO Circe codec" should "round-trip" in {
    val shc = ScoreHistoryCongressDO(scoreId, 118, 0.82f)
    shc.asJson.as[ScoreHistoryCongressDO] shouldBe Right(shc)
  }

  "ScoreHistoryCongressTopicDO Circe codec" should "round-trip" in {
    val shct = ScoreHistoryCongressTopicDO(scoreId, 118, "healthcare", 0.9f)
    shct.asJson.as[ScoreHistoryCongressTopicDO] shouldBe Right(shct)
  }

  "ScoreHistoryHighlightDO Circe codec" should "round-trip" in {
    val shh = ScoreHistoryHighlightDO(scoreId, "hr-1234-118", "healthcare", "progressive", "Yea", 0.95f)
    shh.asJson.as[ScoreHistoryHighlightDO] shouldBe Right(shh)
  }

}
