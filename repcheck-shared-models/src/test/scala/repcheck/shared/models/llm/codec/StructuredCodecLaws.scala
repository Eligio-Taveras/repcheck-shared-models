package repcheck.shared.models.llm.codec

import scala.jdk.CollectionConverters._

import io.circe.Json

import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.{JsonSchemaFactory, SpecVersion}
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

  private val mapper  = new ObjectMapper()
  private val factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)
  private val schema  = factory.getSchema(sc.jsonSchema.noSpaces)

  private def schemaErrors(instance: Json): Set[String] =
    schema.validate(mapper.readTree(instance.noSpaces)).asScala.map(_.getMessage).toSet

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
