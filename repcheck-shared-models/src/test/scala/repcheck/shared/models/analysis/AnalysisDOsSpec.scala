package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.Decoder
import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AnalysisDOsSpec extends AnyFlatSpec with Matchers {

  private val now = Instant.parse("2024-06-01T12:00:00Z")

  // ---- BillConceptGroupDO ----

  private val sampleConceptGroup = BillConceptGroupDO(
    id = 1L,
    versionId = 2L,
    billId = 1L,
    label = "Healthcare Provisions",
    conceptSummary = "Provisions expanding healthcare coverage and reducing costs.",
    embedding = None,
    taxonomyVersion = Some(1),
    createdAt = Some(now),
    updatedAt = Some(now),
    decompositionSnapshotVersion = Some(3),
    runId = Some(42L),
  )

  "BillConceptGroupDO Circe codec" should "round-trip with all fields populated" in {
    sampleConceptGroup.asJson.as[BillConceptGroupDO] shouldBe Right(sampleConceptGroup)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = sampleConceptGroup.copy(
      embedding = None,
      taxonomyVersion = None,
      createdAt = None,
      updatedAt = None,
      decompositionSnapshotVersion = None,
      runId = None,
    )
    minimal.asJson.as[BillConceptGroupDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillConceptGroupDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillConceptGroupDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayGet
    implicitly[Read[BillConceptGroupDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayPut
    implicitly[Write[BillConceptGroupDO]].shouldBe(a[AnyRef])
  }

  // ---- BillConceptGroupSectionDO ----

  private val sampleConceptGroupSection = BillConceptGroupSectionDO(
    conceptGroupId = 1L,
    sectionId = 2L,
  )

  "BillConceptGroupSectionDO Circe codec" should "round-trip" in {
    sampleConceptGroupSection.asJson.as[BillConceptGroupSectionDO] shouldBe Right(sampleConceptGroupSection)
  }

  it should "fail on missing required field" in {
    decode[BillConceptGroupSectionDO]("""{"conceptGroupId":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillConceptGroupSectionDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    implicitly[Read[BillConceptGroupSectionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    implicitly[Write[BillConceptGroupSectionDO]].shouldBe(a[AnyRef])
  }

  // ---- BillAnalysisDO ----

  private val sampleAnalysis = BillAnalysisDO(
    id = 1L,
    billId = 2L,
    versionId = 3L,
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
      id = 4L,
      billId = 3L,
      versionId = 5L,
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
    decode[BillAnalysisDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillAnalysisDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayGet
    import com.repcheck.utils.doobie.DoobieArrayCodecs._
    implicitly[Read[BillAnalysisDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayPut
    import com.repcheck.utils.doobie.DoobieArrayCodecs._
    implicitly[Write[BillAnalysisDO]].shouldBe(a[AnyRef])
  }

  // ---- BillConceptSummaryDO ----

  private val sampleConceptSummary = BillConceptSummaryDO(
    id = 1L,
    analysisId = 2L,
    billId = 4L,
    conceptGroupId = Some(3L),
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
    decode[BillConceptSummaryDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillConceptSummaryDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayGet
    import com.repcheck.utils.doobie.DoobieArrayCodecs._
    implicitly[Read[BillConceptSummaryDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayPut
    import com.repcheck.utils.doobie.DoobieArrayCodecs._
    implicitly[Write[BillConceptSummaryDO]].shouldBe(a[AnyRef])
  }

  // ---- BillAnalysisTopicDO ----

  private val sampleTopic = BillAnalysisTopicDO(
    id = 1L,
    analysisId = 2L,
    billId = 5L,
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
    decode[BillAnalysisTopicDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillAnalysisTopicDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    implicitly[Read[BillAnalysisTopicDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    implicitly[Write[BillAnalysisTopicDO]].shouldBe(a[AnyRef])
  }

  // ---- BillFindingDO ----

  private val sampleFinding = BillFindingDO(
    id = 1L,
    billId = 6L,
    analysisId = Some(2L),
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
    decode[BillFindingDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillFindingDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayGet
    implicitly[Read[BillFindingDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayPut
    implicitly[Write[BillFindingDO]].shouldBe(a[AnyRef])
  }

  // ---- BillFiscalEstimateDO ----

  private val sampleFiscalEstimate = BillFiscalEstimateDO(
    id = 1L,
    analysisId = 2L,
    billId = 7L,
    conceptGroupId = Some(3L),
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
    decode[BillFiscalEstimateDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillFiscalEstimateDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.DoobieArrayCodecs._
    implicitly[Read[BillFiscalEstimateDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.DoobieArrayCodecs._
    implicitly[Write[BillFiscalEstimateDO]].shouldBe(a[AnyRef])
  }

  // ---- AmendmentFindingDO ----

  private val sampleAmendmentFinding = AmendmentFindingDO(
    id = 1L,
    amendmentId = 1L,
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
    decode[AmendmentFindingDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[AmendmentFindingDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayGet
    implicitly[Read[AmendmentFindingDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayPut
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
    implicitly[Read[FindingTypeDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    implicitly[Write[FindingTypeDO]].shouldBe(a[AnyRef])
  }

  // ---- BillConceptGroupTaxonomyDO ----

  private val sampleGroupTaxonomy = BillConceptGroupTaxonomyDO(groupId = 1L, taxonomyNodeId = 7L, score = 0.82f)

  "BillConceptGroupTaxonomyDO Circe codec" should "round-trip" in {
    sampleGroupTaxonomy.asJson.as[BillConceptGroupTaxonomyDO] shouldBe Right(sampleGroupTaxonomy)
  }

  it should "fail on missing required field" in {
    decode[BillConceptGroupTaxonomyDO]("""{"groupId":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillConceptGroupTaxonomyDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    implicitly[Read[BillConceptGroupTaxonomyDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    implicitly[Write[BillConceptGroupTaxonomyDO]].shouldBe(a[AnyRef])
  }

  // ---- TaxonomyNodeDO ----

  private val sampleTaxonomyNode = TaxonomyNodeDO(
    id = 1L,
    name = "Healthcare",
    parentId = None,
    description = "Laws affecting medical care, insurance, and public health.",
    embedding = None,
    version = 1,
    status = "active",
  )

  "TaxonomyNodeDO Circe codec" should "round-trip with all fields populated" in {
    sampleTaxonomyNode.asJson.as[TaxonomyNodeDO] shouldBe Right(sampleTaxonomyNode)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = sampleTaxonomyNode.copy(parentId = Some(3L), embedding = None)
    minimal.asJson.as[TaxonomyNodeDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[TaxonomyNodeDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[TaxonomyNodeDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayGet
    implicitly[Read[TaxonomyNodeDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayPut
    implicitly[Write[TaxonomyNodeDO]].shouldBe(a[AnyRef])
  }

  // ---- PreLlmMetadataSnapshotDO ----

  private val sampleSnapshot = PreLlmMetadataSnapshotDO(snapshotVersion = 7, createdAt = Some(now), status = "active")

  "PreLlmMetadataSnapshotDO Circe codec" should "round-trip with all fields populated" in {
    sampleSnapshot.asJson.as[PreLlmMetadataSnapshotDO] shouldBe Right(sampleSnapshot)
  }

  it should "round-trip with createdAt as None" in {
    val minimal = sampleSnapshot.copy(createdAt = None)
    minimal.asJson.as[PreLlmMetadataSnapshotDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[PreLlmMetadataSnapshotDO]("""{"snapshotVersion":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[PreLlmMetadataSnapshotDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Read[PreLlmMetadataSnapshotDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Write[PreLlmMetadataSnapshotDO]].shouldBe(a[AnyRef])
  }

  // ---- PreLlmMetadataSnapshotMemberDO ----

  private val sampleSnapshotMember =
    PreLlmMetadataSnapshotMemberDO(snapshotVersion = 7, versionId = 1001L, subjectCount = 3)

  "PreLlmMetadataSnapshotMemberDO Circe codec" should "round-trip" in {
    sampleSnapshotMember.asJson.as[PreLlmMetadataSnapshotMemberDO] shouldBe Right(sampleSnapshotMember)
  }

  it should "fail on missing required field" in {
    decode[PreLlmMetadataSnapshotMemberDO]("""{"snapshotVersion":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[PreLlmMetadataSnapshotMemberDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    implicitly[Read[PreLlmMetadataSnapshotMemberDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    implicitly[Write[PreLlmMetadataSnapshotMemberDO]].shouldBe(a[AnyRef])
  }

  // ---- BillDecompositionRunDO ----

  private val sampleRun = BillDecompositionRunDO(
    id = 1L,
    snapshotVersion = 7,
    orchestratorVersion = "0.1.0",
    embedderVersion = "qwen3-0.6b-1024",
    clustererVersion = "routing-v2",
    promptVersion = "summarize-v1",
    status = "completed",
    startedAt = Some(now),
    completedAt = Some(Instant.parse("2024-06-01T12:30:00Z")),
    workflowRunId = Some(555L),
  )

  "BillDecompositionRunDO Circe codec" should "round-trip with all fields populated" in {
    sampleRun.asJson.as[BillDecompositionRunDO] shouldBe Right(sampleRun)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = sampleRun.copy(startedAt = None, completedAt = None, workflowRunId = None)
    minimal.asJson.as[BillDecompositionRunDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillDecompositionRunDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillDecompositionRunDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Read[BillDecompositionRunDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Write[BillDecompositionRunDO]].shouldBe(a[AnyRef])
  }

}
