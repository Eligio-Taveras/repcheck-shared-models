package repcheck.shared.models.user

import io.circe.parser.{decode, decodeAccumulating}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MemberBillStanceDOSpec extends AnyFlatSpec with Matchers {

  private val sample = MemberBillStanceDO(
    memberId = "M000303",
    billId = "hr-1234-118",
    voteId = Some("vote-456"),
    position = Some("Yea"),
    voteType = Some("recorded"),
    voteDate = Some("2024-03-15"),
    congress = Some(118),
    topics = List("healthcare", "defense", "education"),
  )

  "MemberBillStanceDO Circe codec" should "round-trip with topics list" in {
    sample.asJson.as[MemberBillStanceDO] shouldBe Right(sample)
  }

  it should "round-trip with empty topics list" in {
    val noTopics = sample.copy(topics = List.empty)
    noTopics.asJson.as[MemberBillStanceDO] shouldBe Right(noTopics)
  }

  it should "round-trip with all optional fields as None" in {
    val minimal = MemberBillStanceDO(
      memberId = "M000303",
      billId = "hr-1234-118",
      voteId = None,
      position = None,
      voteType = None,
      voteDate = None,
      congress = None,
      topics = List("taxation"),
    )
    minimal.asJson.as[MemberBillStanceDO] shouldBe Right(minimal)
  }

  it should "fail on missing required fields" in {
    decode[MemberBillStanceDO]("""{"memberId":"M000303"}""").isLeft shouldBe true
  }

  it should "decode with absent optional fields" in {
    val json = """{"memberId":"M000303","billId":"hr-1234-118","topics":[]}"""
    decode[MemberBillStanceDO](json) shouldBe Right(
      MemberBillStanceDO(
        memberId = "M000303",
        billId = "hr-1234-118",
        voteId = None,
        position = None,
        voteType = None,
        voteDate = None,
        congress = None,
        topics = List.empty,
      )
    )
  }

  it should "round-trip with congress Some and other fields None" in {
    val stance = MemberBillStanceDO(
      memberId = "M000303",
      billId = "hr-5678-117",
      voteId = None,
      position = None,
      voteType = None,
      voteDate = None,
      congress = Some(117),
      topics = List("defense"),
    )
    stance.asJson.as[MemberBillStanceDO] shouldBe Right(stance)
  }

  it should "decodeAccumulating valid JSON" in {
    val json = """{"memberId":"M000303","billId":"hr-1234-118","topics":[]}"""
    decodeAccumulating[MemberBillStanceDO](json).isValid shouldBe true
  }

  it should "decodeAccumulating invalid field types" in {
    val json = """{"memberId":123,"billId":456,"topics":"not-a-list"}"""
    decodeAccumulating[MemberBillStanceDO](json).isInvalid should be(true)
  }

  it should "decodeAccumulating missing required fields" in {
    decodeAccumulating[MemberBillStanceDO]("""{}""").isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    implicitly[Read[MemberBillStanceDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    implicitly[Write[MemberBillStanceDO]].shouldBe(a[AnyRef])
  }

}
