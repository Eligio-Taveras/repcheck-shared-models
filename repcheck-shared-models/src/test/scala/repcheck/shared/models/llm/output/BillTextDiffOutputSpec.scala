package repcheck.shared.models.llm.output

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.bill.ChangeType

class BillTextDiffOutputSpec extends AnyFlatSpec with Matchers {

  private val sampleChange = SectionChange(
    sectionId = "sec-1",
    changeType = ChangeType.Modified,
    previousText = Some("Original text of section 1"),
    currentText = Some("Updated text of section 1"),
    description = "Language was strengthened regarding enforcement",
  )

  private val sampleDiff = BillTextDiffOutput(
    previousVersionCode = "ih",
    currentVersionCode = "rh",
    billId = "118-HR-1234",
    sections = List(
      sampleChange,
      SectionChange(
        sectionId = "sec-3",
        changeType = ChangeType.Added,
        previousText = None,
        currentText = Some("New section added"),
        description = "New reporting requirement added",
      ),
    ),
    summary = "The bill was amended to strengthen enforcement and add reporting requirements",
    significanceScore = 0.75,
  )

  "SectionChange Circe codec" should "round-trip" in {
    val json    = sampleChange.asJson
    val decoded = json.as[SectionChange]
    decoded shouldBe Right(sampleChange)
  }

  it should "accumulate decode errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[SectionChange].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  "BillTextDiffOutput Circe codec" should "round-trip with populated sections" in {
    val json    = sampleDiff.asJson
    val decoded = json.as[BillTextDiffOutput]
    decoded shouldBe Right(sampleDiff)
  }

  it should "decode from LLM-style JSON" in {
    val llmJson =
      """{
        |  "previousVersionCode": "ih",
        |  "currentVersionCode": "rh",
        |  "billId": "118-HR-5678",
        |  "sections": [
        |    {
        |      "sectionId": "sec-2",
        |      "changeType": "removed",
        |      "previousText": "Deleted text",
        |      "currentText": null,
        |      "description": "Section was removed entirely"
        |    }
        |  ],
        |  "summary": "A section was removed",
        |  "significanceScore": 0.5
        |}""".stripMargin

    val result = decode[BillTextDiffOutput](llmJson)
    result.isRight shouldBe true
    val diff = result.toOption.flatMap(Option(_))
    diff.isDefined shouldBe true
    diff.foreach { d =>
      d.billId shouldBe "118-HR-5678"
      d.sections.length shouldBe 1
      d.sections.headOption.foreach(_.changeType shouldBe ChangeType.Removed)
    }
  }

  it should "fail on missing required field" in {
    decode[BillTextDiffOutput]("""{"previousVersionCode":"ih"}""").isLeft shouldBe true
  }

  it should "accumulate decode errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillTextDiffOutput].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
