package repcheck.shared.models.congress.amendment

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SponsorTypeSpec extends AnyFlatSpec with Matchers {

  "SponsorType.fromString" should "parse 'member' (case-insensitive)" in {
    val _ = SponsorType.fromString("member") shouldBe Right(SponsorType.Member)
    val _ = SponsorType.fromString("MEMBER") shouldBe Right(SponsorType.Member)
    SponsorType.fromString("Member") shouldBe Right(SponsorType.Member)
  }

  it should "parse 'committee' (case-insensitive)" in {
    val _ = SponsorType.fromString("committee") shouldBe Right(SponsorType.Committee)
    val _ = SponsorType.fromString("COMMITTEE") shouldBe Right(SponsorType.Committee)
    SponsorType.fromString("Committee") shouldBe Right(SponsorType.Committee)
  }

  it should "reject unrecognized values" in {
    val result = SponsorType.fromString("unknown")
    val _      = result.isLeft shouldBe true
    result.left.map(_.getMessage).left.map(_.contains("unknown")) shouldBe Left(true)
  }

  "SponsorType Circe codec" should "round-trip Member" in {
    val json    = (SponsorType.Member: SponsorType).asJson
    val decoded = json.as[SponsorType]
    val _       = json.asString shouldBe Some("member")
    decoded shouldBe Right(SponsorType.Member)
  }

  it should "round-trip Committee" in {
    val json    = (SponsorType.Committee: SponsorType).asJson
    val decoded = json.as[SponsorType]
    val _       = json.asString shouldBe Some("committee")
    decoded shouldBe Right(SponsorType.Committee)
  }

  it should "fail on invalid JSON string" in {
    decode[SponsorType](""""badvalue"""").isLeft shouldBe true
  }

  "SponsorType enum" should "have apiValue matching DB enum values" in {
    val _ = SponsorType.Member.apiValue shouldBe "member"
    SponsorType.Committee.apiValue shouldBe "committee"
  }

  "SponsorType Doobie Get/Put" should "be derivable" in {
    import doobie._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    val _ = implicitly[Get[SponsorType]].shouldBe(a[AnyRef])
    implicitly[Put[SponsorType]].shouldBe(a[AnyRef])
  }

}
