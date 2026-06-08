package repcheck.shared.models.llm.codec

import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}

import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import sttp.apispec.circe._
import sttp.tapir.Schema
import sttp.tapir.docs.apispec.schema.TapirSchemaToJsonSchema

/**
 * The codec for one schema-bound (LLM-boundary) type, defined in that type's companion — no central registry. Schema,
 * example, sample, and codec all come from this one place and cannot drift (the §10c #5b law verifies it).
 *
 * `sampleGen` MUST be total — no `Gen.fail` or over-filtering that can exhaust — since it is the single source for both
 * the property tests and any varied sample shown to the model; `sampleJson` relies on that totality.
 */
trait StructuredCodec[A] {
  def encoder: Encoder[A]
  def decoder: Decoder[A]
  def schema: Schema[A]
  def canonicalExample: A
  def sampleGen: Gen[A]

  /** JSON Schema derived from `schema`; Options are nullable to match circe's `None` → `null` encoding. */
  final def jsonSchema: Json =
    TapirSchemaToJsonSchema(schema, markOptionsAsNullable = true).asJson.deepDropNullValues

  final def exampleJson: Json = encoder(canonicalExample)

  /** A deterministic sample for `seed`, encoded — total because `sampleGen` is total (see trait doc). */
  final def sampleJson(seed: Long): Json =
    encoder(sampleGen.pureApply(Gen.Parameters.default, Seed(seed)))

}

object StructuredCodec {

  def apply[A](using sc: StructuredCodec[A]): StructuredCodec[A] = sc

  /** Build a codec from a type's `schema`, `canonicalExample`, and `sampleGen`; the circe codec comes from scope. */
  def instance[A](schema: Schema[A], canonicalExample: A, sampleGen: Gen[A])(using
    encoder: Encoder[A],
    decoder: Decoder[A],
  ): StructuredCodec[A] = new Impl(encoder, decoder, schema, canonicalExample, sampleGen)

  final private class Impl[A](
    val encoder: Encoder[A],
    val decoder: Decoder[A],
    val schema: Schema[A],
    val canonicalExample: A,
    val sampleGen: Gen[A],
  ) extends StructuredCodec[A]

}
