package repcheck.shared.models.llm.output

import io.circe.parser.{decode, decodeAccumulating}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.llm.{ImpactSeverity, PorkType, StanceType}

class LlmOutputSpec extends AnyFlatSpec with Matchers {

  "BillSummaryOutput Circe codec" should "round-trip" in {
    val output = BillSummaryOutput("A bill about healthcare", "8th grade", List("Expands coverage", "Reduces costs"))
    output.asJson.as[BillSummaryOutput] shouldBe Right(output)
  }

  it should "fail on missing required field" in {
    decode[BillSummaryOutput]("""{"summary":"test","readingLevel":"5th grade"}""").isLeft shouldBe true
  }

  "TopicClassificationOutput Circe codec" should "round-trip" in {
    val output = TopicClassificationOutput(List(TopicScore("healthcare", 0.95), TopicScore("defense", 0.3)))
    output.asJson.as[TopicClassificationOutput] shouldBe Right(output)
  }

  "TopicScore Circe codec" should "round-trip" in {
    val ts = TopicScore("education", 0.87)
    ts.asJson.as[TopicScore] shouldBe Right(ts)
  }

  "StanceClassificationOutput Circe codec" should "round-trip" in {
    val output = StanceClassificationOutput(
      List(TopicStance("healthcare", StanceType.Progressive, 0.9, "Supports universal coverage"))
    )
    output.asJson.as[StanceClassificationOutput] shouldBe Right(output)
  }

  "TopicStance Circe codec" should "round-trip" in {
    val ts = TopicStance("defense", StanceType.Conservative, 0.85, "Strong defense spending")
    ts.asJson.as[TopicStance] shouldBe Right(ts)
  }

  "PorkDetectionOutput Circe codec" should "round-trip" in {
    val output = PorkDetectionOutput(
      List(PorkFinding(PorkType.Earmark, "Local bridge project", "Section 4", ImpactSeverity.Medium))
    )
    output.asJson.as[PorkDetectionOutput] shouldBe Right(output)
  }

  "PorkFinding Circe codec" should "round-trip" in {
    val pf = PorkFinding(PorkType.Rider, "Tax provision", "Section 12", ImpactSeverity.High)
    pf.asJson.as[PorkFinding] shouldBe Right(pf)
  }

  "ImpactAnalysisOutput Circe codec" should "round-trip" in {
    val output =
      ImpactAnalysisOutput(List(ImpactItem("Veterans", "positive", "Increased benefits", ImpactSeverity.High)))
    output.asJson.as[ImpactAnalysisOutput] shouldBe Right(output)
  }

  "ImpactItem Circe codec" should "round-trip" in {
    val item = ImpactItem("Small businesses", "negative", "Increased regulation", ImpactSeverity.Low)
    item.asJson.as[ImpactItem] shouldBe Right(item)
  }

  "FiscalEstimateOutput Circe codec" should "round-trip" in {
    val output = FiscalEstimateOutput("$1.2 trillion", "10 years", List("GDP growth 2%", "No recession"), 0.75)
    output.asJson.as[FiscalEstimateOutput] shouldBe Right(output)
  }

  it should "fail on missing required field" in {
    decode[FiscalEstimateOutput]("""{"estimatedCost":"$1B","timeframe":"5 years"}""").isLeft shouldBe true
  }

  "AlignmentScoreOutput Circe codec" should "round-trip" in {
    val output = AlignmentScoreOutput(
      topicScores = List(TopicAlignmentScore("healthcare", 0.85, "Aligned on coverage expansion")),
      overallScore = 0.8,
      highlights = List(AlignmentHighlight("hr-1234", "healthcare", "progressive", "Yea", 0.95)),
      reasoning = "Strong alignment on social issues",
    )
    output.asJson.as[AlignmentScoreOutput] shouldBe Right(output)
  }

  "TopicAlignmentScore Circe codec" should "round-trip" in {
    val tas = TopicAlignmentScore("defense", 0.6, "Moderate alignment")
    tas.asJson.as[TopicAlignmentScore] shouldBe Right(tas)
  }

  "AlignmentHighlight Circe codec" should "round-trip" in {
    val ah = AlignmentHighlight("s-456", "environment", "bipartisan", "Nay", 0.3)
    ah.asJson.as[AlignmentHighlight] shouldBe Right(ah)
  }

  "Sample LLM output JSON" should "decode BillSummaryOutput" in {
    val json =
      """{"summary":"This bill expands Medicare.","readingLevel":"10th grade","keyPoints":["Dental","Vision"]}"""
    decode[BillSummaryOutput](json).isRight shouldBe true
  }

  it should "decode StanceClassificationOutput" in {
    val json =
      """{"stances":[{"topic":"healthcare","stance":"progressive","confidence":0.92,"reasoning":"Expands programs"}]}"""
    val result = decode[StanceClassificationOutput](json)
    val _      = result.isRight shouldBe true
    result.foreach(output => output.stances.headOption.map(_.stance) shouldBe Some(StanceType.Progressive))
  }

  it should "decode PorkDetectionOutput" in {
    val json =
      """{"findings":[{"porkType":"earmark","description":"Highway funding","affectedSection":"Section 301","severity":"medium"}]}"""
    decode[PorkDetectionOutput](json).isRight shouldBe true
  }

  "TopicScore decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[TopicScore]("""{"topic":"healthcare","confidence":0.9}""").isValid shouldBe true
  }

  "TopicClassificationOutput decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[TopicClassificationOutput]("""{"topics":[]}""").isValid shouldBe true
  }

  "TopicStance decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[TopicStance](
      """{"topic":"health","stance":"progressive","confidence":0.9,"reasoning":"test"}"""
    ).isValid shouldBe true
  }

  "StanceClassificationOutput decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[StanceClassificationOutput]("""{"stances":[]}""").isValid shouldBe true
  }

  "PorkFinding decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[PorkFinding](
      """{"porkType":"earmark","description":"x","affectedSection":"S1","severity":"medium"}"""
    ).isValid shouldBe true
  }

  "PorkDetectionOutput decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[PorkDetectionOutput]("""{"findings":[]}""").isValid shouldBe true
  }

  "ImpactItem decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[ImpactItem](
      """{"affectedGroup":"Veterans","impactType":"positive","description":"x","severity":"high"}"""
    ).isValid shouldBe true
  }

  "ImpactAnalysisOutput decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[ImpactAnalysisOutput]("""{"impacts":[]}""").isValid shouldBe true
  }

  "FiscalEstimateOutput decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[FiscalEstimateOutput](
      """{"estimatedCost":"$1B","timeframe":"5y","assumptions":[],"confidence":0.8}"""
    ).isValid shouldBe true
  }

  "TopicAlignmentScore decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[TopicAlignmentScore](
      """{"topic":"health","score":0.9,"explanation":"test"}"""
    ).isValid shouldBe true
  }

  "AlignmentHighlight decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[AlignmentHighlight](
      """{"billId":"hr-1","topic":"health","stance":"progressive","vote":"Yea","alignment":0.9}"""
    ).isValid shouldBe true
  }

  "AlignmentScoreOutput decodeAccumulating" should "succeed on valid JSON" in {
    decodeAccumulating[AlignmentScoreOutput](
      """{"topicScores":[],"overallScore":0.8,"highlights":[],"reasoning":"test"}"""
    ).isValid shouldBe true
  }

}
