package repcheck.shared.models.user

import java.time.Instant
import java.util.UUID

import io.circe.parser.decodeAccumulating
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScoringInfraDOsSpec extends AnyFlatSpec with Matchers {

  private val uid       = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
  private val findingId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000")
  private val stanceId  = UUID.fromString("770e8400-e29b-41d4-a716-446655440000")
  private val instant   = Instant.parse("2024-06-01T14:00:00Z")

  // --- UserLegislatorPairingDO ---

  "UserLegislatorPairingDO Circe codec" should "round-trip with all fields" in {
    val pairing = UserLegislatorPairingDO(
      userId = uid,
      memberId = 1L,
      state = "CA",
      district = Some(12),
      chamber = "House",
      pairedAt = Some(instant),
      validatedAt = Some(instant),
    )
    pairing.asJson.as[UserLegislatorPairingDO] shouldBe Right(pairing)
  }

  it should "round-trip with None fields" in {
    val pairing = UserLegislatorPairingDO(uid, 2L, "NY", None, "Senate", None, None)
    pairing.asJson.as[UserLegislatorPairingDO] shouldBe Right(pairing)
  }

  it should "decodeAccumulating valid JSON" in {
    val json = s"""{"userId":"$uid","memberId":1,"state":"CA","chamber":"House"}"""
    decodeAccumulating[UserLegislatorPairingDO](json).isValid shouldBe true
  }

  "UserLegislatorPairingDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[UserLegislatorPairingDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[UserLegislatorPairingDO]].shouldBe(a[AnyRef])
  }

  // --- MemberBillStanceTopicDO ---

  "MemberBillStanceTopicDO Circe codec" should "round-trip with all fields" in {
    val topic = MemberBillStanceTopicDO(
      id = stanceId,
      memberId = 1L,
      billId = 2L,
      voteId = Some(3L),
      topic = "healthcare",
      stanceDirection = "Progressive",
      reasoning = Some("Voted in favor of expanded coverage"),
      reasoningEmbedding = Some(Array(0.1f, 0.2f, 0.3f)),
      findingId = Some(findingId),
      confidence = Some(0.95),
      conceptSummary = Some("Healthcare expansion"),
      createdAt = Some(instant),
    )
    val decoded = topic.asJson.as[MemberBillStanceTopicDO]
    decoded.isRight shouldBe true
    decoded.foreach { result =>
      result.id shouldBe topic.id
      result.stanceDirection shouldBe topic.stanceDirection
      result.reasoningEmbedding.map(_.toSeq) shouldBe topic.reasoningEmbedding.map(_.toSeq)
    }
  }

  it should "round-trip with None fields" in {
    val topic = MemberBillStanceTopicDO(
      stanceId,
      1L,
      2L,
      None,
      "defense",
      "Conservative",
      None,
      None,
      None,
      None,
      None,
      None,
    )
    topic.asJson.as[MemberBillStanceTopicDO] shouldBe Right(topic)
  }

  it should "decodeAccumulating valid JSON" in {
    val json =
      s"""{"id":"$stanceId","memberId":1,"billId":2,"topic":"health","stanceDirection":"Neutral"}"""
    decodeAccumulating[MemberBillStanceTopicDO](json).isValid shouldBe true
  }

  "MemberBillStanceTopicDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec._
    implicitly[Read[MemberBillStanceTopicDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec._
    implicitly[Write[MemberBillStanceTopicDO]].shouldBe(a[AnyRef])
  }

  // --- UserBillAlignmentDO ---

  "UserBillAlignmentDO Circe codec" should "round-trip with all fields" in {
    val alignment = UserBillAlignmentDO(
      userId = uid,
      billId = 2L,
      topic = "healthcare",
      userStanceScore = 0.85,
      billStanceDirection = "Progressive",
      alignmentScore = 0.92,
      reasoning = Some("Strong alignment on coverage expansion"),
      reasoningEmbedding = Some(Array(0.4f, 0.5f, 0.6f)),
      findingId = Some(findingId),
      computedAt = Some(instant),
    )
    val decoded = alignment.asJson.as[UserBillAlignmentDO]
    decoded.isRight shouldBe true
    decoded.foreach { result =>
      result.alignmentScore shouldBe alignment.alignmentScore
      result.reasoningEmbedding.map(_.toSeq) shouldBe alignment.reasoningEmbedding.map(_.toSeq)
    }
  }

  it should "round-trip with None fields" in {
    val alignment = UserBillAlignmentDO(uid, 2L, "defense", 0.5, "Conservative", 0.3, None, None, None, None)
    alignment.asJson.as[UserBillAlignmentDO] shouldBe Right(alignment)
  }

  it should "decodeAccumulating valid JSON" in {
    val json =
      s"""{"userId":"$uid","billId":2,"topic":"t","userStanceScore":0.5,"billStanceDirection":"Neutral","alignmentScore":0.5}"""
    decodeAccumulating[UserBillAlignmentDO](json).isValid shouldBe true
  }

  "UserBillAlignmentDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec._
    implicitly[Read[UserBillAlignmentDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec._
    implicitly[Write[UserBillAlignmentDO]].shouldBe(a[AnyRef])
  }

  // --- UserAmendmentAlignmentDO ---

  "UserAmendmentAlignmentDO Circe codec" should "round-trip with all fields" in {
    val alignment = UserAmendmentAlignmentDO(
      userId = uid,
      amendmentId = 3L,
      billId = Some(2L),
      topic = "healthcare",
      userStanceScore = 0.75,
      amendmentStanceDirection = "Progressive",
      alignmentScore = 0.88,
      reasoning = Some("Amendment aligns with user stance"),
      reasoningEmbedding = Some(Array(0.7f, 0.8f, 0.9f)),
      findingId = Some(findingId),
      computedAt = Some(instant),
    )
    val decoded = alignment.asJson.as[UserAmendmentAlignmentDO]
    decoded.isRight shouldBe true
    decoded.foreach { result =>
      result.amendmentStanceDirection shouldBe alignment.amendmentStanceDirection
      result.reasoningEmbedding.map(_.toSeq) shouldBe alignment.reasoningEmbedding.map(_.toSeq)
    }
  }

  it should "round-trip with None fields" in {
    val alignment =
      UserAmendmentAlignmentDO(uid, 4L, None, "defense", 0.5, "Conservative", 0.3, None, None, None, None)
    alignment.asJson.as[UserAmendmentAlignmentDO] shouldBe Right(alignment)
  }

  it should "decodeAccumulating valid JSON" in {
    val json =
      s"""{"userId":"$uid","amendmentId":3,"topic":"t","userStanceScore":0.5,"amendmentStanceDirection":"Neutral","alignmentScore":0.5}"""
    decodeAccumulating[UserAmendmentAlignmentDO](json).isValid shouldBe true
  }

  "UserAmendmentAlignmentDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec._
    implicitly[Read[UserAmendmentAlignmentDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec._
    implicitly[Write[UserAmendmentAlignmentDO]].shouldBe(a[AnyRef])
  }

  // --- StanceMaterializationStatusDO ---

  "StanceMaterializationStatusDO Circe codec" should "round-trip with all fields" in {
    val status = StanceMaterializationStatusDO(
      billId = 2L,
      hasVotes = true,
      hasAnalysis = true,
      allPassesCompleted = false,
      votesUpdatedAt = Some(instant),
      analysisCompletedAt = Some(instant),
      stancesMaterializedAt = Some(instant),
      lastScoringRunAt = Some(instant),
    )
    status.asJson.as[StanceMaterializationStatusDO] shouldBe Right(status)
  }

  it should "round-trip with None fields" in {
    val status = StanceMaterializationStatusDO(2L, false, false, false, None, None, None, None)
    status.asJson.as[StanceMaterializationStatusDO] shouldBe Right(status)
  }

  it should "decodeAccumulating valid JSON" in {
    val json =
      """{"billId":2,"hasVotes":true,"hasAnalysis":false,"allPassesCompleted":false}"""
    decodeAccumulating[StanceMaterializationStatusDO](json).isValid shouldBe true
  }

  "StanceMaterializationStatusDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[StanceMaterializationStatusDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[StanceMaterializationStatusDO]].shouldBe(a[AnyRef])
  }

}
