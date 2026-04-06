package repcheck.shared.models.congress.dos.bill

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BillDOSpec extends AnyFlatSpec with Matchers {

  private val sampleBill = BillDO(
    billId = 1L,
    naturalKey = "118-hr-1234",
    congress = 118,
    billType = "hr",
    number = "1234",
    title = "Test Bill Title",
    originChamber = Some("House"),
    originChamberCode = Some("H"),
    introducedDate = Some("2024-01-15"),
    policyArea = Some("Health"),
    latestActionDate = Some("2024-03-01"),
    latestActionText = Some("Referred to committee"),
    constitutionalAuthorityText = Some("Article I, Section 8"),
    sponsorMemberId = Some(1L),
    textUrl = Some("https://congress.gov/bill/118/hr/1234/text"),
    textFormat = Some("xml"),
    textVersionType = Some("Introduced"),
    textDate = Some("2024-01-15"),
    textContent = Some("Full text of the bill"),
    textEmbedding = None,
    summaryText = Some("A summary of the bill"),
    summaryActionDesc = Some("Introduced in House"),
    summaryActionDate = Some("2024-01-15"),
    updateDate = Some("2024-03-01T12:00:00Z"),
    updateDateIncludingText = Some("2024-03-01T12:00:00Z"),
    legislationUrl = Some("https://congress.gov/bill/118/hr/1234"),
    apiUrl = Some("https://api.congress.gov/v3/bill/118/hr/1234"),
    createdAt = Some(Instant.parse("2024-01-15T10:30:00Z")),
    updatedAt = Some(Instant.parse("2024-06-01T14:00:00Z")),
    latestTextVersionId = None,
  )

  "BillDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleBill.asJson
    val decoded = json.as[BillDO]
    decoded shouldBe Right(sampleBill)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = BillDO(
      billId = 2L,
      naturalKey = "118-hr-5678",
      congress = 118,
      billType = "hr",
      number = "5678",
      title = "Minimal Bill",
      originChamber = None,
      originChamberCode = None,
      introducedDate = None,
      policyArea = None,
      latestActionDate = None,
      latestActionText = None,
      constitutionalAuthorityText = None,
      sponsorMemberId = None,
      textUrl = None,
      textFormat = None,
      textVersionType = None,
      textDate = None,
      textContent = None,
      textEmbedding = None,
      summaryText = None,
      summaryActionDesc = None,
      summaryActionDate = None,
      updateDate = None,
      updateDateIncludingText = None,
      legislationUrl = None,
      apiUrl = None,
      createdAt = None,
      updatedAt = None,
      latestTextVersionId = None,
    )
    minimal.asJson.as[BillDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillDO]("""{"billId":1,"naturalKey":"118-hr-1234","congress":118}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayGet
    implicitly[Read[BillDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayPut
    implicitly[Write[BillDO]].shouldBe(a[AnyRef])
  }

  "Bill DO package" should "accumulate decode errors for BillDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for BillCosponsorDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillCosponsorDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for BillSubjectDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillSubjectDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
