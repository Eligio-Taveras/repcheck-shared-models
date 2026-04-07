package repcheck.shared.models.llm

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FindingTypeSpec extends AnyFlatSpec with Matchers {

  "FindingType.fromString" should "parse all 11 values" in {
    val _ = FindingType.fromString("TopicExtraction") shouldBe Right(FindingType.TopicExtraction)
    val _ = FindingType.fromString("BillSummary") shouldBe Right(FindingType.BillSummary)
    val _ = FindingType.fromString("PolicyAnalysis") shouldBe Right(FindingType.PolicyAnalysis)
    val _ = FindingType.fromString("StanceDetection") shouldBe Right(FindingType.StanceDetection)
    val _ = FindingType.fromString("Pork") shouldBe Right(FindingType.Pork)
    val _ = FindingType.fromString("Rider") shouldBe Right(FindingType.Rider)
    val _ = FindingType.fromString("Lobbying") shouldBe Right(FindingType.Lobbying)
    val _ = FindingType.fromString("Constitutional") shouldBe Right(FindingType.Constitutional)
    val _ = FindingType.fromString("TextVersionDiff") shouldBe Right(FindingType.TextVersionDiff)
    val _ = FindingType.fromString("Impact") shouldBe Right(FindingType.Impact)
    FindingType.fromString("FiscalEstimate") shouldBe Right(FindingType.FiscalEstimate)
  }

  it should "have exactly 11 values" in {
    FindingType.values.length shouldBe 11
  }

  it should "be case-insensitive" in {
    val _ = FindingType.fromString("topicextraction") shouldBe Right(FindingType.TopicExtraction)
    val _ = FindingType.fromString("BILLSUMMARY") shouldBe Right(FindingType.BillSummary)
    FindingType.fromString("pork") shouldBe Right(FindingType.Pork)
  }

  it should "return Left for unknown values" in {
    val result = FindingType.fromString("Unknown")
    result.isLeft shouldBe true
  }

  "FindingType Circe codec" should "round-trip values" in {
    FindingType.values.foreach { ft =>
      val json    = ft.asJson
      val decoded = json.as[FindingType]
      decoded shouldBe Right(ft)
    }
  }

}
