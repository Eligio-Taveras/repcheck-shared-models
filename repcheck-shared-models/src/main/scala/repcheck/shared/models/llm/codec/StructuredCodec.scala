package repcheck.shared.models.llm.codec

import io.circe.{Decoder, Encoder, Json}

import org.scalacheck.Gen
import org.scalacheck.rng.Seed

/**
 * One instance per schema-bound (LLM-boundary) type, defined in that type's companion object — no central registry.
 * Bundles the circe codec, the JSON Schema, a curated canonical example, and a ScalaCheck generator so that schema,
 * example, sample, and codec all come from one place and cannot drift (the §10c #5b law verifies this).
 *
 * `sampleGen` is the single source for both the property tests AND any varied sample shown to the model, exactly as
 * `canonicalExample`/`exampleJson` is the curated example shown in production prompts.
 */
trait StructuredCodec[A] {
  def encoder: Encoder[A]
  def decoder: Decoder[A]
  def jsonSchema: Json
  def canonicalExample: A
  def sampleGen: Gen[A]

  /** The curated example, encoded — valid against `jsonSchema` by construction (law-checked). */
  final def exampleJson: Json = encoder(canonicalExample)

  /** A deterministic generated sample (by seed), encoded — for varied LLM samples and reproducible tests. */
  final def sampleJson(seed: Long): Json =
    encoder(sampleGen.pureApply(Gen.Parameters.default, Seed(seed)))

}

object StructuredCodec {
  def apply[A](using sc: StructuredCodec[A]): StructuredCodec[A] = sc
}
