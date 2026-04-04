package repcheck.shared.models.codecs

import java.time.{LocalDate, ZoneId, ZoneOffset, ZonedDateTime}
import java.util.UUID

import io.circe.Json
import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DateTimeCodecsSpec extends AnyFlatSpec with Matchers {

  import DateTimeCodecs.*

  // --- ZonedDateTime ---

  "ZonedDateTime codec" should "round-trip UTC datetime" in {
    val dt      = ZonedDateTime.of(2024, 3, 15, 10, 30, 0, 0, ZoneOffset.UTC)
    val json    = dt.asJson
    val decoded = json.as[ZonedDateTime]
    decoded shouldBe Right(dt)
  }

  it should "round-trip datetime with timezone" in {
    val dt      = ZonedDateTime.of(2024, 6, 20, 14, 45, 30, 0, ZoneId.of("America/New_York"))
    val json    = dt.asJson
    val decoded = json.as[ZonedDateTime]
    decoded.map(_.toInstant) shouldBe Right(dt.toInstant)
  }

  it should "decode ISO-8601 zoned datetime string" in {
    val result = decode[ZonedDateTime](""""2024-03-15T10:30:00Z"""")
    result.isRight shouldBe true
  }

  it should "fail on invalid datetime string" in {
    val result = decode[ZonedDateTime](""""not-a-date"""")
    result.isLeft shouldBe true
  }

  it should "encode to ISO-8601 format" in {
    val dt   = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    val json = dt.asJson
    json.isString shouldBe true
  }

  // --- LocalDate ---

  "LocalDate codec" should "round-trip a date" in {
    val date    = LocalDate.of(2024, 3, 15)
    val json    = date.asJson
    val decoded = json.as[LocalDate]
    decoded shouldBe Right(date)
  }

  it should "decode yyyy-MM-dd format" in {
    val result = decode[LocalDate](""""2024-12-25"""")
    result shouldBe Right(LocalDate.of(2024, 12, 25))
  }

  it should "fail on invalid date string" in {
    val result = decode[LocalDate](""""not-a-date"""")
    result.isLeft shouldBe true
  }

  it should "encode to ISO-8601 date format" in {
    val date = LocalDate.of(2024, 1, 1)
    val json = date.asJson
    json shouldBe Json.fromString("2024-01-01")
  }

  // --- UUID ---

  "UUID codec" should "round-trip a UUID" in {
    val uuid    = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
    val json    = uuid.asJson
    val decoded = json.as[UUID]
    decoded shouldBe Right(uuid)
  }

  it should "decode standard UUID string" in {
    val result = decode[UUID](""""550e8400-e29b-41d4-a716-446655440000"""")
    result shouldBe Right(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
  }

  it should "fail on invalid UUID string" in {
    val result = decode[UUID](""""not-a-uuid"""")
    result.isLeft shouldBe true
  }

  it should "encode UUID to string" in {
    val uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
    val json = uuid.asJson
    json shouldBe Json.fromString("550e8400-e29b-41d4-a716-446655440000")
  }

}
