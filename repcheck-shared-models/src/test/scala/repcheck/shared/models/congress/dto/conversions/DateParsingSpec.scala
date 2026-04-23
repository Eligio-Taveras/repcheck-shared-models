package repcheck.shared.models.congress.dto.conversions

import java.time.{Instant, LocalDate}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Unit spec for [[DateParsing]]. Exhaustive over every input shape the downstream pipeline can receive from
 * Congress.gov + senate.gov feeds — including the OffsetDateTime-with-timezone form that breaks `Instant.parse` /
 * `LocalDate.parse` outright, discovered during votes-pipeline E2E testing.
 */
class DateParsingSpec extends AnyFlatSpec with Matchers {

  // ============================================================================
  // toLocalDate
  // ============================================================================

  "toLocalDate" should "parse a pure YYYY-MM-DD string" in {
    DateParsing.toLocalDate(Some("2025-09-08")) shouldBe Some(LocalDate.of(2025, 9, 8))
  }

  it should "parse an OffsetDateTime string by extracting the date portion" in {
    DateParsing.toLocalDate(Some("2025-09-08T18:56:00-04:00")) shouldBe Some(LocalDate.of(2025, 9, 8))
  }

  it should "parse a UTC-Z ISO datetime by extracting the date portion" in {
    DateParsing.toLocalDate(Some("2025-09-08T22:56:00Z")) shouldBe Some(LocalDate.of(2025, 9, 8))
  }

  it should "parse a ZonedDateTime string by extracting the date portion" in {
    DateParsing.toLocalDate(Some("2025-09-08T18:56:00-04:00[America/New_York]")) shouldBe
      Some(LocalDate.of(2025, 9, 8))
  }

  it should "return None for an unparseable string" in {
    DateParsing.toLocalDate(Some("not-a-date")) shouldBe None
  }

  it should "return None for None input" in {
    DateParsing.toLocalDate(None) shouldBe None
  }

  it should "return None for an empty string" in {
    DateParsing.toLocalDate(Some("")) shouldBe None
  }

  // ============================================================================
  // toInstant
  // ============================================================================

  "toInstant" should "parse a strict ISO UTC (Z suffix) string" in {
    DateParsing.toInstant(Some("2025-09-09T18:53:19Z")) shouldBe Some(Instant.parse("2025-09-09T18:53:19Z"))
  }

  it should "parse an OffsetDateTime string and normalise to UTC" in {
    // 18:53 EDT (-04:00) == 22:53 UTC
    DateParsing.toInstant(Some("2025-09-09T18:53:19-04:00")) shouldBe
      Some(Instant.parse("2025-09-09T22:53:19Z"))
  }

  it should "parse a ZonedDateTime string and normalise to UTC" in {
    DateParsing.toInstant(Some("2025-09-09T18:53:19-04:00[America/New_York]")) shouldBe
      Some(Instant.parse("2025-09-09T22:53:19Z"))
  }

  it should "parse a date-only string as midnight UTC (legacy fallback)" in {
    DateParsing.toInstant(Some("2025-09-09")) shouldBe Some(Instant.parse("2025-09-09T00:00:00Z"))
  }

  it should "return None for an unparseable string" in {
    DateParsing.toInstant(Some("not-a-date")) shouldBe None
  }

  it should "return None for None input" in {
    DateParsing.toInstant(None) shouldBe None
  }

  it should "return None for an empty string" in {
    DateParsing.toInstant(Some("")) shouldBe None
  }

}
