package repcheck.shared.models.congress.dto.bill

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TextVersionDTOsSpec extends AnyFlatSpec with Matchers {

  "FormatDTO" should "round-trip encoding 'type' field" in {
    val dto  = FormatDTO(type_ = "Formatted Text", url = "https://example.com/text.htm")
    val json = dto.asJson
    json.hcursor.downField("type").as[String] shouldBe Right("Formatted Text")
    json.hcursor.downField("url").as[String] shouldBe Right("https://example.com/text.htm")
    json.as[FormatDTO] shouldBe Right(dto)
  }

  it should "decode JSON with 'type' field" in {
    val json   = """{"type":"PDF","url":"https://example.com/doc.pdf"}"""
    val result = decode[FormatDTO](json)
    result shouldBe Right(FormatDTO(type_ = "PDF", url = "https://example.com/doc.pdf"))
  }

  "TextVersionDTO" should "round-trip encoding 'type' field" in {
    val dto = TextVersionDTO(
      date = Some("2024-01-15"),
      formats = Some(
        List(
          FormatDTO("Formatted Text", "https://example.com/text.htm"),
          FormatDTO("PDF", "https://example.com/text.pdf"),
        )
      ),
      type_ = Some("Introduced in House"),
    )
    val json = dto.asJson
    json.hcursor.downField("type").as[Option[String]] shouldBe Right(Some("Introduced in House"))
    json.as[TextVersionDTO] shouldBe Right(dto)
  }

  it should "decode with missing optional fields" in {
    val json   = """{}"""
    val result = decode[TextVersionDTO](json)
    result shouldBe Right(TextVersionDTO(date = None, formats = None, type_ = None))
  }

  it should "decode JSON with 'type' field mapped to type_" in {
    val json   = """{"date":"2024-01-15","type":"Enrolled Bill"}"""
    val result = decode[TextVersionDTO](json)
    result.map(_.type_) shouldBe Right(Some("Enrolled Bill"))
  }

}
