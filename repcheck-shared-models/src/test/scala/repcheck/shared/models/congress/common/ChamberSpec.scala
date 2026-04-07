package repcheck.shared.models.congress.common

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ChamberSpec extends AnyFlatSpec with Matchers {

  "Chamber.fromString" should "parse canonical values" in {
    val _ = Chamber.fromString("House") shouldBe Right(Chamber.House)
    Chamber.fromString("Senate") shouldBe Right(Chamber.Senate)
  }

  it should "be case-insensitive" in {
    val _ = Chamber.fromString("HOUSE") shouldBe Right(Chamber.House)
    val _ = Chamber.fromString("house") shouldBe Right(Chamber.House)
    val _ = Chamber.fromString("House") shouldBe Right(Chamber.House)
    val _ = Chamber.fromString("SENATE") shouldBe Right(Chamber.Senate)
    Chamber.fromString("senate") shouldBe Right(Chamber.Senate)
  }

  it should "accept alias 'House of Representatives'" in {
    Chamber.fromString("House of Representatives") shouldBe Right(Chamber.House)
  }

  it should "return Left for unknown values" in {
    val result = Chamber.fromString("Unknown")
    val _      = result.isLeft shouldBe true
    result.left.map(_.getMessage) should matchPattern {
      case Left(msg: String) if msg.contains("Unknown") =>
    }
  }

  "Chamber Circe codec" should "round-trip values" in {
    Chamber.values.foreach { c =>
      val json    = c.asJson
      val decoded = json.as[Chamber]
      decoded shouldBe Right(c)
    }
  }

  it should "encode to apiValue" in {
    val _ = Chamber.House.asJson.asString shouldBe Some("House")
    Chamber.Senate.asJson.asString shouldBe Some("Senate")
  }

}
