package repcheck.shared.models.congress.common

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FormatTypeSpec extends AnyFlatSpec with Matchers {

  "FormatType.fromString" should "parse canonical values" in {
    val _ = FormatType.fromString("Formatted Text") shouldBe Right(FormatType.FormattedText)
    val _ = FormatType.fromString("PDF") shouldBe Right(FormatType.PDF)
    FormatType.fromString("Formatted XML") shouldBe Right(FormatType.FormattedXml)
  }

  it should "accept concatenated aliases" in {
    val _ = FormatType.fromString("FormattedText") shouldBe Right(FormatType.FormattedText)
    FormatType.fromString("FormattedXml") shouldBe Right(FormatType.FormattedXml)
  }

  it should "be case-insensitive" in {
    val _ = FormatType.fromString("pdf") shouldBe Right(FormatType.PDF)
    FormatType.fromString("FORMATTED TEXT") shouldBe Right(FormatType.FormattedText)
  }

  it should "return Left for unknown values" in {
    val result = FormatType.fromString("HTML")
    result.isLeft shouldBe true
  }

  "FormatType Circe codec" should "round-trip values" in {
    FormatType.values.foreach { ft =>
      val json    = ft.asJson
      val decoded = json.as[FormatType]
      decoded shouldBe Right(ft)
    }
  }

}
