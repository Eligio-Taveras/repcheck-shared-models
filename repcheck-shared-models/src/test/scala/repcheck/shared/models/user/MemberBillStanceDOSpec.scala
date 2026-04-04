package repcheck.shared.models.user

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MemberBillStanceDOSpec extends AnyFlatSpec with Matchers {

  private val sample = MemberBillStanceDO(
    memberId = "M000303", billId = "hr-1234-118",
    voteId = Some("vote-456"), position = Some("Yea"),
    voteType = Some("recorded"), voteDate = Some("2024-03-15"),
    congress = Some(118), topics = List("healthcare", "defense", "education")
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
      memberId = "M000303", billId = "hr-1234-118",
      voteId = None, position = None, voteType = None,
      voteDate = None, congress = None, topics = List("taxation")
    )
    minimal.asJson.as[MemberBillStanceDO] shouldBe Right(minimal)
  }

  it should "fail on missing required fields" in {
    decode[MemberBillStanceDO]("""{"memberId":"M000303"}""").isLeft shouldBe true
  }

}
