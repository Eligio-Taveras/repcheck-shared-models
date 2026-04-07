package repcheck.shared.models.congress.dos.amendment

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AmendmentDOSpec extends AnyFlatSpec with Matchers {

  private val sampleAmendment = AmendmentDO(
    amendmentId = 1L,
    naturalKey = "118-hamdt-1",
    congress = 118,
    amendmentType = Some("hamdt"),
    number = "1",
    billId = Some(2L),
    chamber = Some("House"),
    description = Some("An amendment to improve section 3"),
    purpose = Some("To strike section 3 and replace with new language"),
    sponsorMemberId = Some(3L),
    submittedDate = Some("2024-02-15"),
    latestActionDate = Some("2024-03-01"),
    latestActionText = Some("Amendment agreed to by voice vote"),
    updateDate = Some("2024-03-01T12:00:00Z"),
    apiUrl = Some("https://api.congress.gov/v3/amendment/118/hamdt/1"),
    createdAt = Some(Instant.parse("2024-02-15T10:30:00Z")),
    updatedAt = Some(Instant.parse("2024-06-01T14:00:00Z")),
  )

  "AmendmentDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleAmendment.asJson
    val decoded = json.as[AmendmentDO]
    decoded shouldBe Right(sampleAmendment)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = AmendmentDO(
      amendmentId = 4L,
      naturalKey = "118-samdt-5",
      congress = 118,
      amendmentType = None,
      number = "5",
      billId = None,
      chamber = None,
      description = None,
      purpose = None,
      sponsorMemberId = None,
      submittedDate = None,
      latestActionDate = None,
      latestActionText = None,
      updateDate = None,
      apiUrl = None,
      createdAt = None,
      updatedAt = None,
    )
    minimal.asJson.as[AmendmentDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[AmendmentDO]("""{"amendmentId":"118-hamdt-1","congress":118}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Read[AmendmentDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    implicitly[Write[AmendmentDO]].shouldBe(a[AnyRef])
  }

  "Amendment DO package" should "accumulate decode errors for AmendmentDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[AmendmentDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
