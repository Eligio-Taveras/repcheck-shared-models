package repcheck.shared.models.llm.codec

import io.circe.Json

import net.reactivecore.cjs.{DocumentValidator, Loader}
import org.scalacheck.Arbitrary
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

/**
 * The §10c #5b StructuredCodec law, as a reusable abstract suite. For the `canonicalExample` AND every `sampleGen`
 * sample: `decode(encode(a)) == a` (round-trip) AND `encode(a)` validates against `jsonSchema` — so the schema,
 * example, sample-generator, and codec a type ships can never silently diverge. One concrete subclass per schema-bound
 * type.
 */
abstract class StructuredCodecLaws[A](name: String)(using sc: StructuredCodec[A])
    extends AnyFlatSpec
    with Matchers
    with ScalaCheckPropertyChecks {

  private val validator: DocumentValidator =
    Loader.empty.fromJson(sc.jsonSchema) match {
      case Right(v) => v
      case Left(e)  => fail(s"$name: jsonSchema is not a valid JSON Schema: $e")
    }

  private def schemaErrors(instance: Json): Seq[String] =
    validator.validate(instance).violations.map(_.toString)

  private given Arbitrary[A] = Arbitrary(sc.sampleGen)

  s"$name StructuredCodec" should "round-trip and schema-validate the canonical example" in {
    sc.decoder.decodeJson(sc.exampleJson) shouldBe Right(sc.canonicalExample)
    schemaErrors(sc.exampleJson) shouldBe empty
  }

  it should "round-trip and schema-validate every generated sample" in {
    forAll { (a: A) =>
      sc.decoder.decodeJson(sc.encoder(a)) shouldBe Right(a)
      schemaErrors(sc.encoder(a)) shouldBe empty
    }
  }

}
