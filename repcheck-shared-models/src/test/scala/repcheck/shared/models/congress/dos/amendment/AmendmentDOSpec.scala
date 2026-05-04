package repcheck.shared.models.congress.dos.amendment

import java.time.{Instant, LocalDate}

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.amendment.AmendmentType
import repcheck.shared.models.congress.common.Chamber
import repcheck.shared.models.placeholder.HasPlaceholder

class AmendmentDOSpec extends AnyFlatSpec with Matchers {

  private val sampleAmendment = AmendmentDO(
    amendmentId = 1L,
    naturalKey = "118-hamdt-1",
    congress = 118,
    amendmentType = Some(AmendmentType.HAMDT),
    number = "1",
    billId = Some(2L),
    chamber = Chamber.House,
    description = Some("An amendment to improve section 3"),
    purpose = Some("To strike section 3 and replace with new language"),
    sponsorMemberId = Some(3L),
    submittedDate = Some(LocalDate.parse("2024-02-15")),
    proposedDate = Some(LocalDate.parse("2024-02-16")),
    latestActionDate = Some(LocalDate.parse("2024-03-01")),
    latestActionTime = Some("14:30:00"),
    latestActionText = Some("Amendment agreed to by voice vote"),
    updateDate = Some(Instant.parse("2024-03-01T12:00:00Z")),
    apiUrl = Some("https://api.congress.gov/v3/amendment/118/hamdt/1"),
    parentAmendmentId = Some(99L),
    effectiveBillId = Some(2L),
    lastTextCheckAt = Some(Instant.parse("2024-03-02T08:15:00Z")),
    createdAt = Some(Instant.parse("2024-02-15T10:30:00Z")),
    updatedAt = Some(Instant.parse("2024-06-01T14:00:00Z")),
  )

  "AmendmentDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleAmendment.asJson
    val decoded = json.as[AmendmentDO]
    decoded shouldBe Right(sampleAmendment)
  }

  it should "round-trip with optional fields as None and chamber Senate" in {
    val minimal = AmendmentDO(
      amendmentId = 4L,
      naturalKey = "118-samdt-5",
      congress = 118,
      amendmentType = None,
      number = "5",
      billId = None,
      chamber = Chamber.Senate,
      description = None,
      purpose = None,
      sponsorMemberId = None,
      submittedDate = None,
      proposedDate = None,
      latestActionDate = None,
      latestActionTime = None,
      latestActionText = None,
      updateDate = None,
      apiUrl = None,
      parentAmendmentId = None,
      effectiveBillId = None,
      lastTextCheckAt = None,
      createdAt = None,
      updatedAt = None,
    )
    minimal.asJson.as[AmendmentDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[AmendmentDO]("""{"amendmentId":"118-hamdt-1","congress":118}""").isLeft shouldBe true
  }

  it should "fail decoding when chamber is missing (NOT NULL contract per L9)" in {
    val json =
      """{"amendmentId":1,"naturalKey":"118-hamdt-1","congress":118,"number":"1"}"""
    decode[AmendmentDO](json).isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Read[AmendmentDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Write[AmendmentDO]].shouldBe(a[AnyRef])
  }

  "Amendment DO package" should "accumulate decode errors for AmendmentDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[AmendmentDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  "AmendmentDO placeholder" should "use natural key, defaulted Chamber.House sentinel, and None for everything else" in {
    val placeholder = HasPlaceholder[AmendmentDO].placeholder("118-samdt-99")
    val _           = placeholder.naturalKey shouldBe "118-samdt-99"
    val _           = placeholder.amendmentId shouldBe 0L
    val _           = placeholder.congress shouldBe 0
    val _           = placeholder.amendmentType shouldBe None
    val _           = placeholder.number shouldBe ""
    val _           = placeholder.chamber shouldBe Chamber.House
    val _           = placeholder.billId shouldBe None
    val _           = placeholder.proposedDate shouldBe None
    val _           = placeholder.latestActionTime shouldBe None
    val _           = placeholder.parentAmendmentId shouldBe None
    val _           = placeholder.effectiveBillId shouldBe None
    placeholder.lastTextCheckAt shouldBe None
  }

}
