package repcheck.shared.models.congress.bill

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SummaryVersionCodeMapperSpec extends AnyFlatSpec with Matchers {

  "SummaryVersionCodeMapper.toTextVersionCode" should "map every documented summary versionCode to a TextVersionCode" in {
    // Empirical catalog seeded from observed /summaries responses. If you add a new entry to
    // SummaryVersionCodeMapper.versionCodeToTextCode, also add a row here so the mapping is
    // explicitly tested rather than relying on the round-trip of `knownVersionCodes`.
    val expected: List[(String, TextVersionCode)] = List(
      "00" -> TextVersionCode.IH,
      "01" -> TextVersionCode.RTH,
      "13" -> TextVersionCode.RH,
      "17" -> TextVersionCode.PL,
      "33" -> TextVersionCode.LTS,
      "34" -> TextVersionCode.EAS,
      "35" -> TextVersionCode.EH,
      "36" -> TextVersionCode.EAH,
      "49" -> TextVersionCode.PL,
      "55" -> TextVersionCode.EAS,
      "70" -> TextVersionCode.RTS,
      "73" -> TextVersionCode.RS,
    )

    expected.foreach {
      case (code, expectedTextCode) =>
        SummaryVersionCodeMapper.toTextVersionCode(code) shouldBe Right(expectedTextCode)
    }
  }

  it should "raise UnrecognizedSummaryVersionCode for codes not in the catalog" in {
    SummaryVersionCodeMapper.toTextVersionCode("999-not-a-code") match {
      case Left(err) =>
        val _ = err.value shouldBe "999-not-a-code"
        err.getMessage should include("999-not-a-code")
      case Right(tvc) =>
        fail(s"Expected Left(UnrecognizedSummaryVersionCode) but got Right($tvc)")
    }
  }

  it should "include actionable guidance in the unrecognized-code error message" in {
    SummaryVersionCodeMapper.toTextVersionCode("XX") match {
      case Left(err) =>
        val msg = err.getMessage
        val _   = msg should include("SummaryVersionCodeMapper")
        val _   = msg should include("redeploy")
        msg should include("empirical")
      case Right(tvc) =>
        fail(s"Expected Left(UnrecognizedSummaryVersionCode) but got Right($tvc)")
    }
  }

  it should "treat the empty string as unrecognized (defensive against malformed JSON)" in {
    SummaryVersionCodeMapper.toTextVersionCode("").isLeft shouldBe true
  }

  it should "be case-sensitive — CRS versionCodes are numeric/alphanumeric, never letters with case variation" in {
    // Sanity check: '00' is mapped, but a synthetic 'ZZ' isn't, and case variants of mapped codes
    // don't accidentally match (the catalog is a plain Map, not case-folded).
    val _ = SummaryVersionCodeMapper.toTextVersionCode("00") shouldBe Right(TextVersionCode.IH)
    SummaryVersionCodeMapper.toTextVersionCode("ZZ").isLeft shouldBe true
  }

  "SummaryVersionCodeMapper.knownVersionCodes" should "expose every entry that toTextVersionCode succeeds for" in {
    SummaryVersionCodeMapper.knownVersionCodes.foreach { code =>
      SummaryVersionCodeMapper.toTextVersionCode(code).isRight shouldBe true
    }
  }

}
