package repcheck.shared.models.congress.member

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MemberTypeSpec extends AnyFlatSpec with Matchers {

  "MemberType.fromString" should "parse canonical apiValue values" in {
    val _ = MemberType.fromString("Representative") shouldBe Right(MemberType.Representative)
    val _ = MemberType.fromString("Senator") shouldBe Right(MemberType.Senator)
    val _ = MemberType.fromString("Delegate") shouldBe Right(MemberType.Delegate)
    MemberType.fromString("Resident Commissioner") shouldBe Right(MemberType.ResidentCommissioner)
  }

  it should "accept short aliases" in {
    val _ = MemberType.fromString("rep") shouldBe Right(MemberType.Representative)
    val _ = MemberType.fromString("sen") shouldBe Right(MemberType.Senator)
    MemberType.fromString("del") shouldBe Right(MemberType.Delegate)
  }

  it should "be case-insensitive" in {
    val _ = MemberType.fromString("REPRESENTATIVE") shouldBe Right(MemberType.Representative)
    val _ = MemberType.fromString("senator") shouldBe Right(MemberType.Senator)
    val _ = MemberType.fromString("REP") shouldBe Right(MemberType.Representative)
    val _ = MemberType.fromString("SEN") shouldBe Right(MemberType.Senator)
    MemberType.fromString("DEL") shouldBe Right(MemberType.Delegate)
  }

  it should "return Left for unknown values" in {
    val result = MemberType.fromString("Governor")
    val _      = result.isLeft shouldBe true
    result.left.map(_.getMessage) should matchPattern {
      case Left(msg: String) if msg.contains("Governor") =>
    }
  }

  "MemberType Circe codec" should "round-trip values" in {
    MemberType.values.foreach { mt =>
      val json    = mt.asJson
      val decoded = json.as[MemberType]
      decoded shouldBe Right(mt)
    }
  }

  it should "encode to apiValue" in {
    val _ = MemberType.Representative.asJson.asString shouldBe Some("Representative")
    MemberType.ResidentCommissioner.asJson.asString shouldBe Some("Resident Commissioner")
  }

  it should "fail to decode unknown JSON value" in {
    val json    = io.circe.Json.fromString("Governor")
    val decoded = json.as[MemberType]
    decoded.isLeft shouldBe true
  }

}
