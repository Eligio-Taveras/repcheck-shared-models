package repcheck.shared.models.user.score

import java.time.Instant
import java.util.UUID

import io.circe.parser.{decode, decodeAccumulating}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScoreHistoryDOSpec extends AnyFlatSpec with Matchers {

  private val scoreId = UUID.fromString("770e8400-e29b-41d4-a716-446655440000")
  private val uid     = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
  private val instant = Instant.parse("2024-06-01T14:00:00Z")

  "ScoreHistoryDO Circe codec" should "round-trip with all optional fields Some" in {
    val sh = ScoreHistoryDO(
      scoreId,
      uid,
      "M000303",
      Some(instant),
      0.85f,
      Some("vote_cast"),
      Some("Strong alignment on healthcare votes"),
    )
    sh.asJson.as[ScoreHistoryDO] shouldBe Right(sh)
  }

  it should "round-trip with all optional fields None" in {
    val sh = ScoreHistoryDO(scoreId, uid, "M000303", None, 0.5f, None, None)
    sh.asJson.as[ScoreHistoryDO] shouldBe Right(sh)
  }

  it should "round-trip with computedAt Some and triggerEvent None" in {
    val sh = ScoreHistoryDO(scoreId, uid, "M000303", Some(instant), 0.7f, None, Some("reasoning"))
    sh.asJson.as[ScoreHistoryDO] shouldBe Right(sh)
  }

  it should "round-trip with computedAt None and triggerEvent Some" in {
    val sh = ScoreHistoryDO(scoreId, uid, "M000303", None, 0.6f, Some("bill_analyzed"), None)
    sh.asJson.as[ScoreHistoryDO] shouldBe Right(sh)
  }

  it should "decode with absent optional fields" in {
    val json = s"""{"scoreId":"$scoreId","userId":"$uid","memberId":"M000303","aggregateScore":0.5}"""
    decode[ScoreHistoryDO](json) shouldBe Right(
      ScoreHistoryDO(scoreId, uid, "M000303", None, 0.5f, None, None)
    )
  }

  it should "fail to decode with missing required fields" in {
    decode[ScoreHistoryDO]("""{"memberId":"M000303"}""").isLeft shouldBe true
  }

  it should "decodeAccumulating valid JSON" in {
    val json = s"""{"scoreId":"$scoreId","userId":"$uid","memberId":"M000303","aggregateScore":0.5}"""
    decodeAccumulating[ScoreHistoryDO](json).isValid shouldBe true
  }

  it should "decodeAccumulating invalid field types" in {
    val json = """{"scoreId":"not-a-uuid","userId":"bad","memberId":123,"aggregateScore":"bad"}"""
    decodeAccumulating[ScoreHistoryDO](json).isInvalid should be(true)
  }

  it should "decodeAccumulating missing required fields" in {
    decodeAccumulating[ScoreHistoryDO]("""{}""").isInvalid should be(true)
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

  "ScoreHistoryCongressDO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[ScoreHistoryCongressDO](
      s"""{"scoreId":"$scoreId","congress":118,"overallScore":0.8}"""
    ).isValid shouldBe true
  }

  "ScoreHistoryCongressTopicDO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[ScoreHistoryCongressTopicDO](
      s"""{"scoreId":"$scoreId","congress":118,"topic":"healthcare","score":0.9}"""
    ).isValid shouldBe true
  }

  "ScoreHistoryHighlightDO decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[ScoreHistoryHighlightDO](
      s"""{"scoreId":"$scoreId","billId":"hr-1-118","topic":"health","stance":"progressive","vote":"Yea","alignment":0.9}"""
    ).isValid shouldBe true
  }

  "ScoreDO decodeAccumulating" should "succeed on valid JSON" in {
    val uid2 = UUID.fromString("660e8400-e29b-41d4-a716-446655440000")
    decodeAccumulating[ScoreDO](
      s"""{"userId":"$uid2","memberId":"M000303","aggregateScore":0.8}"""
    ).isValid shouldBe true
  }

  "ScoreCongressDO decodeAccumulating" should "succeed on valid JSON" in {
    val uid2 = UUID.fromString("660e8400-e29b-41d4-a716-446655440000")
    decodeAccumulating[ScoreCongressDO](
      s"""{"userId":"$uid2","memberId":"M000303","congress":118,"overallScore":0.8}"""
    ).isValid shouldBe true
  }

  "ScoreCongressTopicDO decodeAccumulating" should "succeed on valid JSON" in {
    val uid2 = UUID.fromString("660e8400-e29b-41d4-a716-446655440000")
    decodeAccumulating[ScoreCongressTopicDO](
      s"""{"userId":"$uid2","memberId":"M000303","congress":118,"topic":"health","score":0.8}"""
    ).isValid shouldBe true
  }

  "ScoreTopicDO decodeAccumulating" should "succeed on valid JSON" in {
    val uid2 = UUID.fromString("660e8400-e29b-41d4-a716-446655440000")
    decodeAccumulating[ScoreTopicDO](
      s"""{"userId":"$uid2","memberId":"M000303","topic":"health","score":0.8}"""
    ).isValid shouldBe true
  }

  "ScoreHistoryDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[ScoreHistoryDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[ScoreHistoryDO]].shouldBe(a[AnyRef])
  }

  "ScoreHistoryCongressDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[ScoreHistoryCongressDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[ScoreHistoryCongressDO]].shouldBe(a[AnyRef])
  }

  "ScoreHistoryCongressTopicDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[ScoreHistoryCongressTopicDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[ScoreHistoryCongressTopicDO]].shouldBe(a[AnyRef])
  }

  "ScoreHistoryHighlightDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[ScoreHistoryHighlightDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[ScoreHistoryHighlightDO]].shouldBe(a[AnyRef])
  }

}
