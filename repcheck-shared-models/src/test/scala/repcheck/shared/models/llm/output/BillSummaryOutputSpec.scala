package repcheck.shared.models.llm.output

import io.circe.parser.{decode, decodeAccumulating}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BillSummaryOutputSpec extends AnyFlatSpec with Matchers {

  "BillSummaryOutput Circe codec" should "round-trip with all fields" in {
    val output = BillSummaryOutput(
      summary = "This bill establishes new healthcare standards.",
      readingLevel = "College",
      keyPoints = List("Expands Medicare", "Reduces drug costs", "Adds preventive care"),
    )
    output.asJson.as[BillSummaryOutput] shouldBe Right(output)
  }

  it should "round-trip with empty keyPoints" in {
    val output = BillSummaryOutput(
      summary = "A brief summary.",
      readingLevel = "High School",
      keyPoints = List.empty,
    )
    output.asJson.as[BillSummaryOutput] shouldBe Right(output)
  }

  it should "round-trip with single keyPoint" in {
    val output = BillSummaryOutput(
      summary = "This bill does one thing.",
      readingLevel = "Middle School",
      keyPoints = List("Increases funding"),
    )
    output.asJson.as[BillSummaryOutput] shouldBe Right(output)
  }

  it should "decode from JSON string" in {
    val json =
      """{"summary":"Test summary","readingLevel":"Graduate","keyPoints":["point1","point2"]}"""
    val result = decode[BillSummaryOutput](json)
    result shouldBe Right(BillSummaryOutput("Test summary", "Graduate", List("point1", "point2")))
  }

  it should "fail to decode with missing required fields" in {
    decode[BillSummaryOutput]("""{"summary":"only summary"}""").isLeft shouldBe true
  }

  it should "encode to JSON with correct field names" in {
    val output = BillSummaryOutput("A summary.", "Elementary", List("key point"))
    val json   = output.asJson
    json.hcursor.downField("summary").as[String] shouldBe Right("A summary.")
    json.hcursor.downField("readingLevel").as[String] shouldBe Right("Elementary")
    json.hcursor.downField("keyPoints").as[List[String]] shouldBe Right(List("key point"))
  }

  it should "decodeAccumulating valid JSON" in {
    val json = """{"summary":"test","readingLevel":"High School","keyPoints":[]}"""
    decodeAccumulating[BillSummaryOutput](json).isValid shouldBe true
  }

  it should "decodeAccumulating invalid field type" in {
    val json = """{"summary":123,"readingLevel":"High School","keyPoints":[]}"""
    decodeAccumulating[BillSummaryOutput](json).isInvalid shouldBe true
  }

  it should "decodeAccumulating multiple invalid fields" in {
    val json = """{"summary":123,"readingLevel":456,"keyPoints":"not-a-list"}"""
    decodeAccumulating[BillSummaryOutput](json).isInvalid shouldBe true
  }

  it should "decodeAccumulating missing required fields" in {
    decodeAccumulating[BillSummaryOutput]("""{}""").isInvalid shouldBe true
  }

}
