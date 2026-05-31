package repcheck.shared.models.congress.dos.bill

import java.time.{Instant, LocalDate}

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BillSummaryDOSpec extends AnyFlatSpec with Matchers {

  private val sample = BillSummaryDO(
    id = 1L,
    billId = 1L,
    versionCode = "ih",
    actionDate = Some(LocalDate.parse("2024-01-15")),
    actionDesc = Some("Introduced in House"),
    text = "<p>This bill does the thing.</p>",
    createdAt = Some(Instant.parse("2024-06-01T12:00:00Z")),
    updatedAt = Some(Instant.parse("2024-06-01T12:00:00Z")),
  )

  "BillSummaryDO Circe codec" should "round-trip with all fields populated" in {
    sample.asJson.as[BillSummaryDO] shouldBe Right(sample)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = BillSummaryDO(
      id = 2L,
      billId = 1L,
      versionCode = "ih",
      actionDate = None,
      actionDesc = None,
      text = "summary body",
      createdAt = None,
      updatedAt = None,
    )
    minimal.asJson.as[BillSummaryDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillSummaryDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Read[BillSummaryDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Write[BillSummaryDO]].shouldBe(a[AnyRef])
  }

  it should "accumulate decode errors" in {
    import io.circe.Decoder
    val bad = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    Decoder[BillSummaryDO].decodeAccumulating(bad.hcursor).isInvalid should be(true)
  }

}
