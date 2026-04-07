package repcheck.shared.models.user

import io.circe.parser.{decode, decodeAccumulating}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MemberBillStanceDOSpec extends AnyFlatSpec with Matchers {

  private val sample = MemberBillStanceDO(
    memberId = 1L,
    billId = 2L,
    voteId = Some(3L),
    amendmentId = Some(4L),
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
      memberId = 1L,
      billId = 2L,
      voteId = None,
      amendmentId = None,
      position = None,
      voteType = None,
      voteDate = None,
      congress = None,
      topics = List("taxation"),
    )
    minimal.asJson.as[MemberBillStanceDO] shouldBe Right(minimal)
  }

  it should "fail on missing required fields" in {
    decode[MemberBillStanceDO]("""{"memberId":1}""").isLeft shouldBe true
  }

  it should "decode with absent optional fields" in {
    val json = """{"memberId":1,"billId":2,"topics":[]}"""
    decode[MemberBillStanceDO](json) shouldBe Right(
      MemberBillStanceDO(
        memberId = 1L,
        billId = 2L,
        voteId = None,
        amendmentId = None,
        position = None,
        voteType = None,
        voteDate = None,
        congress = None,
        topics = List.empty,
      )
    )
  }

  it should "round-trip with amendmentId Some and voteId None" in {
    val stance = MemberBillStanceDO(
      memberId = 1L,
      billId = 5L,
      voteId = None,
      amendmentId = Some(6L),
      position = None,
      voteType = None,
      voteDate = None,
      congress = Some(117),
      topics = List("defense"),
    )
    stance.asJson.as[MemberBillStanceDO] shouldBe Right(stance)
  }

  it should "decodeAccumulating valid JSON" in {
    val json = """{"memberId":1,"billId":2,"topics":[]}"""
    decodeAccumulating[MemberBillStanceDO](json).isValid shouldBe true
  }

  it should "decodeAccumulating invalid field types" in {
    val json = """{"memberId":"bad","billId":"bad","topics":"not-a-list"}"""
    decodeAccumulating[MemberBillStanceDO](json).isInvalid should be(true)
  }

  it should "decodeAccumulating missing required fields" in {
    decodeAccumulating[MemberBillStanceDO]("""{}""").isInvalid should be(true)
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    implicitly[Read[MemberBillStanceDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import repcheck.shared.models.codecs.DoobieArrayCodecs._
    implicitly[Write[MemberBillStanceDO]].shouldBe(a[AnyRef])
  }

}
