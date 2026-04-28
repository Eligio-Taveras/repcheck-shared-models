package repcheck.shared.models.congress.common

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PartySpec extends AnyFlatSpec with Matchers {

  "Party.fromString" should "parse canonical values" in {
    val _ = Party.fromString("Democrat") shouldBe Right(Party.Democrat)
    val _ = Party.fromString("Republican") shouldBe Right(Party.Republican)
    val _ = Party.fromString("Independent") shouldBe Right(Party.Independent)
    Party.fromString("Independent Democrat") shouldBe Right(Party.IndependentDemocrat)
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

  it should "accept abbreviation 'I' for Independent (true Independent, no caucus)" in {
    Party.fromString("I") shouldBe Right(Party.Independent)
  }

  it should "accept abbreviation 'ID' for IndependentDemocrat (Independent caucusing with Democrats)" in {
    // Lieberman 2007–2013 is the canonical case. "ID" historically meant Independent-Democrat caucus
    // and is distinct from "I" (true Independent). The DB enums party_type/party_abbreviation_type
    // were extended in db-migrations changeset 033 to carry both 'Independent Democrat' and 'ID'.
    Party.fromString("ID") shouldBe Right(Party.IndependentDemocrat)
  }

  it should "be case-insensitive (incl. multi-word IndependentDemocrat alias)" in {
    val _ = Party.fromString("democrat") shouldBe Right(Party.Democrat)
    val _ = Party.fromString("DEMOCRAT") shouldBe Right(Party.Democrat)
    val _ = Party.fromString("republican") shouldBe Right(Party.Republican)
    val _ = Party.fromString("INDEPENDENT DEMOCRAT") shouldBe Right(Party.IndependentDemocrat)
    Party.fromString("independent democrat") shouldBe Right(Party.IndependentDemocrat)
  }

  it should "preserve apiValue for IndependentDemocrat (matches DB party_type enum value)" in {
    // The Doobie pgEnumStringOpt[Party] writer uses .apiValue. For the DB cast
    // 'Independent Democrat'::party_type to succeed, apiValue must match exactly.
    Party.IndependentDemocrat.apiValue shouldBe "Independent Democrat"
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
