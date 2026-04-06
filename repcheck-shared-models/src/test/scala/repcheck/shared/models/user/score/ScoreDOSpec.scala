package repcheck.shared.models.user.score

import java.time.Instant
import java.util.UUID

import io.circe.parser.decodeAccumulating
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScoreDOSpec extends AnyFlatSpec with Matchers {

  private val uid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

  "ScoreDO Circe codec" should "round-trip with all fields" in {
    val score = ScoreDO(
      userId = uid,
      memberId = 1L,
      aggregateScore = 0.85f,
      status = "scored",
      lastUpdated = Some(Instant.parse("2024-06-01T14:00:00Z")),
      llmModel = Some("claude-3-haiku"),
      totalBills = Some(42),
      totalVotes = Some(38),
      nonOverlappingTopics = List("healthcare", "defense"),
      reasoning = Some("Strong alignment on healthcare votes"),
      reasoningEmbedding = Some(Array(0.1f, 0.2f, 0.3f)),
    )
    val decoded = score.asJson.as[ScoreDO]
    decoded.isRight shouldBe true
    decoded.foreach { result =>
      result.userId shouldBe score.userId
      result.memberId shouldBe score.memberId
      result.aggregateScore shouldBe score.aggregateScore
      result.status shouldBe score.status
      result.lastUpdated shouldBe score.lastUpdated
      result.llmModel shouldBe score.llmModel
      result.totalBills shouldBe score.totalBills
      result.totalVotes shouldBe score.totalVotes
      result.nonOverlappingTopics shouldBe score.nonOverlappingTopics
      result.reasoning shouldBe score.reasoning
      result.reasoningEmbedding.map(_.toSeq) shouldBe score.reasoningEmbedding.map(_.toSeq)
    }
  }

  it should "round-trip with None fields and empty list" in {
    val score = ScoreDO(
      userId = uid,
      memberId = 1L,
      aggregateScore = 0.5f,
      status = "no_overlap",
      lastUpdated = None,
      llmModel = None,
      totalBills = None,
      totalVotes = None,
      nonOverlappingTopics = List.empty,
      reasoning = None,
      reasoningEmbedding = None,
    )
    score.asJson.as[ScoreDO] shouldBe Right(score)
  }

  it should "decodeAccumulating valid JSON" in {
    val json =
      s"""{"userId":"$uid","memberId":1,"aggregateScore":0.8,"status":"scored","nonOverlappingTopics":[]}"""
    decodeAccumulating[ScoreDO](json).isValid shouldBe true
  }

  it should "decodeAccumulating invalid field types" in {
    val json = """{"userId":"bad","memberId":"bad","aggregateScore":"bad","status":1,"nonOverlappingTopics":"bad"}"""
    decodeAccumulating[ScoreDO](json).isInvalid should be(true)
  }

  "ScoreTopicDO Circe codec" should "round-trip" in {
    val st = ScoreTopicDO(uid, 1L, "healthcare", 0.9f)
    st.asJson.as[ScoreTopicDO] shouldBe Right(st)
  }

  it should "decodeAccumulating valid JSON" in {
    decodeAccumulating[ScoreTopicDO](
      s"""{"userId":"$uid","memberId":1,"topic":"health","score":0.8}"""
    ).isValid shouldBe true
  }

  "ScoreCongressDO Circe codec" should "round-trip" in {
    val sc = ScoreCongressDO(uid, 1L, 118, 0.82f, Some(42), Some(38))
    sc.asJson.as[ScoreCongressDO] shouldBe Right(sc)
  }

  it should "round-trip with None fields" in {
    val sc = ScoreCongressDO(uid, 1L, 117, 0.6f, None, None)
    sc.asJson.as[ScoreCongressDO] shouldBe Right(sc)
  }

  it should "decodeAccumulating valid JSON" in {
    decodeAccumulating[ScoreCongressDO](
      s"""{"userId":"$uid","memberId":1,"congress":118,"overallScore":0.8}"""
    ).isValid shouldBe true
  }

  "ScoreCongressTopicDO Circe codec" should "round-trip" in {
    val sct = ScoreCongressTopicDO(uid, 1L, 118, "environment", 0.75f)
    sct.asJson.as[ScoreCongressTopicDO] shouldBe Right(sct)
  }

  it should "decodeAccumulating valid JSON" in {
    decodeAccumulating[ScoreCongressTopicDO](
      s"""{"userId":"$uid","memberId":1,"congress":118,"topic":"health","score":0.8}"""
    ).isValid shouldBe true
  }

  "ScoreDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    import repcheck.shared.models.codecs.VectorCodec._
    implicitly[Read[ScoreDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    import repcheck.shared.models.codecs.VectorCodec._
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
