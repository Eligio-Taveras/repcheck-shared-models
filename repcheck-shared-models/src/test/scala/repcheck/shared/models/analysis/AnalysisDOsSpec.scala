package repcheck.shared.models.analysis

import java.time.Instant
import java.util.UUID

import io.circe.Decoder
import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AnalysisDOsSpec extends AnyFlatSpec with Matchers {

  private val uuid1 = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")
  private val uuid2 = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901")
  private val uuid3 = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012")
  private val now   = Instant.parse("2024-06-01T12:00:00Z")

  // ---- BillConceptGroupDO ----

  private val sampleConceptGroup = BillConceptGroupDO(
    conceptGroupId = uuid1,
    versionId = uuid2,
    billId = "118-hr-1234",
    groupId = "group-1",
    title = "Healthcare Provisions",
    simplifiedText = "Simplified text about healthcare",
    embedding = None,
    createdAt = Some(now),
  )

  "BillConceptGroupDO Circe codec" should "round-trip with all fields populated" in {
    sampleConceptGroup.asJson.as[BillConceptGroupDO] shouldBe Right(sampleConceptGroup)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = sampleConceptGroup.copy(embedding = None, createdAt = None)
    minimal.asJson.as[BillConceptGroupDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillConceptGroupDO]("""{"conceptGroupId":"a1b2c3d4-e5f6-7890-abcd-ef1234567890"}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillConceptGroupDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayGet
    implicitly[Read[BillConceptGroupDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayPut
    implicitly[Write[BillConceptGroupDO]].shouldBe(a[AnyRef])
  }

  // ---- BillConceptGroupSectionDO ----

  private val sampleConceptGroupSection = BillConceptGroupSectionDO(
    conceptGroupId = uuid1,
    sectionId = uuid2,
  )

  "BillConceptGroupSectionDO Circe codec" should "round-trip" in {
    sampleConceptGroupSection.asJson.as[BillConceptGroupSectionDO] shouldBe Right(sampleConceptGroupSection)
  }

  it should "fail on missing required field" in {
    decode[BillConceptGroupSectionDO](
      """{"conceptGroupId":"a1b2c3d4-e5f6-7890-abcd-ef1234567890"}"""
    ).isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillConceptGroupSectionDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[BillConceptGroupSectionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[BillConceptGroupSectionDO]].shouldBe(a[AnyRef])
  }

  // ---- BillAnalysisDO ----

  private val sampleAnalysis = BillAnalysisDO(
    analysisId = uuid1,
    billId = "118-hr-1234",
    versionId = uuid2,
    status = "completed",
    summary = Some("A bill about healthcare reform"),
    topics = List("healthcare", "reform"),
    readingLevel = Some("college"),
    keyPoints = List("Expands coverage", "Reduces costs"),
    passesExecuted = List(1, 2),
    highestModelUsed = Some("claude-3-opus"),
    pass1Model = Some("claude-3-haiku"),
    pass2Model = Some("claude-3-opus"),
    pass3Model = None,
    embedding = None,
    highProfileScore = Some(0.85),
    mediaCoverageLevel = Some(0.7),
    appropriationsEstimate = Some(BigDecimal("1500000000.50")),
    stanceConfidence = Some(0.92),
    routingReasoning = Some("High profile bill requiring detailed analysis"),
    overallConfidence = Some(0.88),
    crossConceptContradictionScore = Some(0.12),
    expectedVoteContention = Some(0.65),
    contradictionDetails = Some("Minor contradiction in section 3 vs section 7"),
    routingReasoningPass2 = Some("Elevated to pass 2 due to complexity"),
    failureReason = None,
    createdAt = Some(now),
    completedAt = Some(Instant.parse("2024-06-01T12:30:00Z")),
  )

  "BillAnalysisDO Circe codec" should "round-trip with all fields populated" in {
    sampleAnalysis.asJson.as[BillAnalysisDO] shouldBe Right(sampleAnalysis)
  }

  it should "round-trip with optional fields as None and empty lists" in {
    val minimal = BillAnalysisDO(
      analysisId = uuid1,
      billId = "118-hr-5678",
      versionId = uuid2,
      status = "pending",
      summary = None,
      topics = List.empty,
      readingLevel = None,
      keyPoints = List.empty,
      passesExecuted = List.empty,
      highestModelUsed = None,
      pass1Model = None,
      pass2Model = None,
      pass3Model = None,
      embedding = None,
      highProfileScore = None,
      mediaCoverageLevel = None,
      appropriationsEstimate = None,
      stanceConfidence = None,
      routingReasoning = None,
      overallConfidence = None,
      crossConceptContradictionScore = None,
      expectedVoteContention = None,
      contradictionDetails = None,
      routingReasoningPass2 = None,
      failureReason = None,
      createdAt = None,
      completedAt = None,
    )
    minimal.asJson.as[BillAnalysisDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillAnalysisDO]("""{"analysisId":"a1b2c3d4-e5f6-7890-abcd-ef1234567890"}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillAnalysisDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayGet
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    implicitly[Read[BillAnalysisDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayPut
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    implicitly[Write[BillAnalysisDO]].shouldBe(a[AnyRef])
  }

  // ---- BillConceptSummaryDO ----

  private val sampleConceptSummary = BillConceptSummaryDO(
    conceptSummaryId = uuid1,
    analysisId = uuid2,
    billId = "118-hr-1234",
    conceptGroupId = Some(uuid3),
    passNumber = 1,
    topics = List("healthcare"),
    summary = Some("Summary of the concept group"),
    readingLevel = Some("high-school"),
    keyPoints = List("Point 1", "Point 2"),
    embedding = None,
    createdAt = Some(now),
  )

  "BillConceptSummaryDO Circe codec" should "round-trip with all fields populated" in {
    sampleConceptSummary.asJson.as[BillConceptSummaryDO] shouldBe Right(sampleConceptSummary)
  }

  it should "round-trip with optional fields as None and empty lists" in {
    val minimal = sampleConceptSummary.copy(
      conceptGroupId = None,
      summary = None,
      readingLevel = None,
      topics = List.empty,
      keyPoints = List.empty,
      embedding = None,
      createdAt = None,
    )
    minimal.asJson.as[BillConceptSummaryDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillConceptSummaryDO]("""{"conceptSummaryId":"a1b2c3d4-e5f6-7890-abcd-ef1234567890"}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillConceptSummaryDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayGet
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    implicitly[Read[BillConceptSummaryDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayPut
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    implicitly[Write[BillConceptSummaryDO]].shouldBe(a[AnyRef])
  }

  // ---- BillAnalysisTopicDO ----

  private val sampleTopic = BillAnalysisTopicDO(
    topicId = uuid1,
    analysisId = uuid2,
    billId = "118-hr-1234",
    conceptGroupId = Some("group-1"),
    passNumber = 1,
    topic = "healthcare",
    confidence = 0.95f,
  )

  "BillAnalysisTopicDO Circe codec" should "round-trip with all fields populated" in {
    sampleTopic.asJson.as[BillAnalysisTopicDO] shouldBe Right(sampleTopic)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = sampleTopic.copy(conceptGroupId = None)
    minimal.asJson.as[BillAnalysisTopicDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillAnalysisTopicDO]("""{"topicId":"a1b2c3d4-e5f6-7890-abcd-ef1234567890"}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillAnalysisTopicDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[BillAnalysisTopicDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[BillAnalysisTopicDO]].shouldBe(a[AnyRef])
  }

  // ---- BillFindingDO ----

  private val sampleFinding = BillFindingDO(
    findingId = uuid1,
    billId = "118-hr-1234",
    analysisId = Some(uuid2),
    findingTypeId = 1,
    passNumber = 1,
    summary = "Finding about healthcare provision",
    details = Some("Detailed analysis of the finding"),
    conceptGroupId = Some("group-1"),
    severity = Some("high"),
    confidence = Some(0.9f),
    affectedSection = Some("Section 3"),
    affectedGroup = Some("group-1"),
    embedding = None,
    llmModel = "claude-3-opus",
    analyzedAt = Some(now),
  )

  "BillFindingDO Circe codec" should "round-trip with all fields populated" in {
    sampleFinding.asJson.as[BillFindingDO] shouldBe Right(sampleFinding)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = sampleFinding.copy(
      analysisId = None,
      details = None,
      conceptGroupId = None,
      severity = None,
      confidence = None,
      affectedSection = None,
      affectedGroup = None,
      embedding = None,
      analyzedAt = None,
    )
    minimal.asJson.as[BillFindingDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillFindingDO]("""{"findingId":"a1b2c3d4-e5f6-7890-abcd-ef1234567890"}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillFindingDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayGet
    implicitly[Read[BillFindingDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayPut
    implicitly[Write[BillFindingDO]].shouldBe(a[AnyRef])
  }

  // ---- BillFiscalEstimateDO ----

  private val sampleFiscalEstimate = BillFiscalEstimateDO(
    fiscalEstimateId = uuid1,
    analysisId = uuid2,
    billId = "118-hr-1234",
    conceptGroupId = Some(uuid3),
    passNumber = 1,
    estimatedCost = "$1.5 billion",
    timeframe = "10 years",
    confidence = 0.75f,
    assumptions = List("GDP growth 2%", "Inflation 3%"),
    llmModel = "claude-3-opus",
    createdAt = Some(now),
  )

  "BillFiscalEstimateDO Circe codec" should "round-trip with all fields populated" in {
    sampleFiscalEstimate.asJson.as[BillFiscalEstimateDO] shouldBe Right(sampleFiscalEstimate)
  }

  it should "round-trip with optional fields as None and empty assumptions" in {
    val minimal = sampleFiscalEstimate.copy(
      conceptGroupId = None,
      assumptions = List.empty,
      createdAt = None,
    )
    minimal.asJson.as[BillFiscalEstimateDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillFiscalEstimateDO]("""{"fiscalEstimateId":"a1b2c3d4-e5f6-7890-abcd-ef1234567890"}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillFiscalEstimateDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    implicitly[Read[BillFiscalEstimateDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    implicitly[Write[BillFiscalEstimateDO]].shouldBe(a[AnyRef])
  }

  // ---- AmendmentFindingDO ----

  private val sampleAmendmentFinding = AmendmentFindingDO(
    findingId = uuid1,
    amendmentId = "118-hamdt-100",
    findingTypeId = 2,
    summary = "Amendment changes scope of section 5",
    details = Some("Detailed amendment analysis"),
    severity = Some("medium"),
    confidence = Some(0.85f),
    affectedSection = Some("Section 5"),
    embedding = None,
    llmModel = "claude-3-haiku",
    analyzedAt = Some(now),
  )

  "AmendmentFindingDO Circe codec" should "round-trip with all fields populated" in {
    sampleAmendmentFinding.asJson.as[AmendmentFindingDO] shouldBe Right(sampleAmendmentFinding)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = sampleAmendmentFinding.copy(
      details = None,
      severity = None,
      confidence = None,
      affectedSection = None,
      embedding = None,
      analyzedAt = None,
    )
    minimal.asJson.as[AmendmentFindingDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[AmendmentFindingDO]("""{"findingId":"a1b2c3d4-e5f6-7890-abcd-ef1234567890"}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[AmendmentFindingDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayGet
    implicitly[Read[AmendmentFindingDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayPut
    implicitly[Write[AmendmentFindingDO]].shouldBe(a[AnyRef])
  }

  // ---- FindingTypeDO ----

  private val sampleFindingType = FindingTypeDO(
    findingTypeId = 1,
    code = "PORK",
    name = "Pork Barrel Spending",
    description = Some("Identifies earmarks and targeted spending provisions"),
  )

  "FindingTypeDO Circe codec" should "round-trip with all fields populated" in {
    sampleFindingType.asJson.as[FindingTypeDO] shouldBe Right(sampleFindingType)
  }

  it should "round-trip with description as None" in {
    val minimal = sampleFindingType.copy(description = None)
    minimal.asJson.as[FindingTypeDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[FindingTypeDO]("""{"findingTypeId":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[FindingTypeDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    implicitly[Read[FindingTypeDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    implicitly[Write[FindingTypeDO]].shouldBe(a[AnyRef])
  }

}
