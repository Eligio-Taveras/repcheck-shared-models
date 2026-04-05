package repcheck.shared.models.congress.dos.committee

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CommitteeDOsSpec extends AnyFlatSpec with Matchers {

  private val sampleCommittee = CommitteeDO(
    committeeCode = "SSFI00",
    name = "Committee on Finance",
    chamber = Some("Senate"),
    committeeType = Some("Standing"),
    parentCommitteeCode = None,
    isCurrent = Some(true),
    updateDate = Some("2024-06-01"),
    createdAt = Some(Instant.parse("2024-01-15T10:30:00Z")),
    updatedAt = Some(Instant.parse("2024-06-01T14:00:00Z")),
  )

  private val sampleCommitteeMember = CommitteeMemberDO(
    committeeCode = "SSFI00",
    memberId = "W000779",
    position = Some("Chairman"),
    side = None,
    rank = None,
    beginDate = Some("2021-01-03"),
    endDate = None,
    congress = Some(118),
    createdAt = Some(Instant.parse("2024-01-15T10:30:00Z")),
    updatedAt = Some(Instant.parse("2024-06-01T14:00:00Z")),
  )

  private val sampleReferral = BillCommitteeReferralDO(
    billId = "118-HR-1234",
    committeeCode = "SSFI00",
    referralDate = Some("2024-01-10"),
    reportDate = Some("2024-03-20"),
    activity = Some("Referred to; Reported by"),
    createdAt = Some(Instant.parse("2024-01-15T10:30:00Z")),
  )

  "CommitteeDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleCommittee.asJson
    val decoded = json.as[CommitteeDO]
    decoded shouldBe Right(sampleCommittee)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = CommitteeDO(
      committeeCode = "TEST00",
      name = "Test Committee",
      chamber = None,
      committeeType = None,
      parentCommitteeCode = None,
      isCurrent = None,
      updateDate = None,
      createdAt = None,
      updatedAt = None,
    )
    minimal.asJson.as[CommitteeDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[CommitteeDO]("""{}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[CommitteeDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[CommitteeDO]].shouldBe(a[AnyRef])
  }

  "CommitteeMemberDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleCommitteeMember.asJson
    val decoded = json.as[CommitteeMemberDO]
    decoded shouldBe Right(sampleCommitteeMember)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = CommitteeMemberDO(
      committeeCode = "HSJU00",
      memberId = "P000197",
      position = None,
      side = None,
      rank = None,
      beginDate = None,
      endDate = None,
      congress = None,
      createdAt = None,
      updatedAt = None,
    )
    minimal.asJson.as[CommitteeMemberDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[CommitteeMemberDO]("""{}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[CommitteeMemberDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[CommitteeMemberDO]].shouldBe(a[AnyRef])
  }

  "BillCommitteeReferralDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleReferral.asJson
    val decoded = json.as[BillCommitteeReferralDO]
    decoded shouldBe Right(sampleReferral)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = BillCommitteeReferralDO(
      billId = "118-S-5678",
      committeeCode = "HSAP00",
      referralDate = None,
      reportDate = None,
      activity = None,
      createdAt = None,
    )
    minimal.asJson.as[BillCommitteeReferralDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillCommitteeReferralDO]("""{}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Read[BillCommitteeReferralDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    implicitly[Write[BillCommitteeReferralDO]].shouldBe(a[AnyRef])
  }

  "Committee DO package" should "accumulate decode errors for CommitteeDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[CommitteeDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for CommitteeMemberDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[CommitteeMemberDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "accumulate decode errors for BillCommitteeReferralDO" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillCommitteeReferralDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
