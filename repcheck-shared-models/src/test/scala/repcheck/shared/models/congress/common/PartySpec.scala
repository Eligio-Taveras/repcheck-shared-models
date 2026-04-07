package repcheck.shared.models.congress.common

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PartySpec extends AnyFlatSpec with Matchers {

  "Party.fromString" should "parse canonical values" in {
    val _ = Party.fromString("Democrat") shouldBe Right(Party.Democrat)
    val _ = Party.fromString("Republican") shouldBe Right(Party.Republican)
    Party.fromString("Independent") shouldBe Right(Party.Independent)
  }

  it should "accept alias 'Democratic' for Democrat" in {
    Party.fromString("Democratic") shouldBe Right(Party.Democrat)
  }

  it should "accept abbreviation 'D' for Democrat" in {
    Party.fromString("D") shouldBe Right(Party.Democrat)
  }

  it should "accept abbreviation 'R' for Republican" in {
    Party.fromString("R") shouldBe Right(Party.Republican)
  }

  it should "accept abbreviation 'I' for Independent" in {
    Party.fromString("I") shouldBe Right(Party.Independent)
  }

  it should "accept abbreviation 'ID' for Independent" in {
    Party.fromString("ID") shouldBe Right(Party.Independent)
  }

  it should "be case-insensitive" in {
    val _ = Party.fromString("democrat") shouldBe Right(Party.Democrat)
    val _ = Party.fromString("DEMOCRAT") shouldBe Right(Party.Democrat)
    Party.fromString("republican") shouldBe Right(Party.Republican)
  }

  it should "return Left for unknown values" in {
    val result = Party.fromString("Libertarian")
    result.isLeft shouldBe true
  }

  "Party Circe codec" should "round-trip values" in {
    Party.values.foreach { p =>
      val json    = p.asJson
      val decoded = json.as[Party]
      decoded shouldBe Right(p)
    }
  }

}
