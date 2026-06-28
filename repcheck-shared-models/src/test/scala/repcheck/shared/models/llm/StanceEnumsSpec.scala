package repcheck.shared.models.llm

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EffectSpec extends AnyFlatSpec with Matchers {

  "Effect.fromString" should "parse every value, case-insensitively" in {
    val _ = Effect.fromString("EXPANDS") shouldBe Right(Effect.Expands)
    val _ = Effect.fromString("restricts") shouldBe Right(Effect.Restricts)
    val _ = Effect.fromString("Creates") shouldBe Right(Effect.Creates)
    val _ = Effect.fromString("eliminates") shouldBe Right(Effect.Eliminates)
    val _ = Effect.fromString("MODIFIES") shouldBe Right(Effect.Modifies)
    Effect.fromString("Reports") shouldBe Right(Effect.Reports)
  }

  it should "have exactly 6 values" in {
    Effect.values.length shouldBe 6
  }

  it should "return Left for unknown values" in {
    Effect.fromString("DELETES").isLeft shouldBe true
  }

  "Effect Circe codec" should "round-trip and serialize to the UPPERCASE apiValue" in {
    val _ = Effect.values.foreach(e => e.asJson.as[Effect] shouldBe Right(e))
    Effect.Expands.asJson.asString shouldBe Some("EXPANDS")
  }

}

class ImpactSpec extends AnyFlatSpec with Matchers {

  "Impact.fromString" should "parse every value, case-insensitively" in {
    val _ = Impact.fromString("POSITIVE") shouldBe Right(Impact.Positive)
    val _ = Impact.fromString("negative") shouldBe Right(Impact.Negative)
    val _ = Impact.fromString("Mixed") shouldBe Right(Impact.Mixed)
    Impact.fromString("NEUTRAL") shouldBe Right(Impact.Neutral)
  }

  it should "have exactly 4 values" in {
    Impact.values.length shouldBe 4
  }

  it should "return Left for unknown values" in {
    Impact.fromString("AMBIVALENT").isLeft shouldBe true
  }

  "Impact Circe codec" should "round-trip and serialize to the UPPERCASE apiValue" in {
    val _ = Impact.values.foreach(i => i.asJson.as[Impact] shouldBe Right(i))
    Impact.Positive.asJson.asString shouldBe Some("POSITIVE")
  }

}

class ScopeSpec extends AnyFlatSpec with Matchers {

  "Scope.fromString" should "parse every value, case-insensitively" in {
    val _ = Scope.fromString("MAJOR") shouldBe Right(Scope.Major)
    val _ = Scope.fromString("moderate") shouldBe Right(Scope.Moderate)
    Scope.fromString("Minor") shouldBe Right(Scope.Minor)
  }

  it should "have exactly 3 values" in {
    Scope.values.length shouldBe 3
  }

  it should "return Left for unknown values" in {
    Scope.fromString("TRIVIAL").isLeft shouldBe true
  }

  "Scope Circe codec" should "round-trip and serialize to the UPPERCASE apiValue" in {
    val _ = Scope.values.foreach(s => s.asJson.as[Scope] shouldBe Right(s))
    Scope.Major.asJson.asString shouldBe Some("MAJOR")
  }

}
