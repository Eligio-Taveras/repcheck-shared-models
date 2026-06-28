package repcheck.shared.models.llm.codec

import io.circe.{Decoder, Encoder}

import sttp.tapir.{Schema, Validator}

/** A value an LLM must return from a fixed, closed set. `apiValue` is the exact wire token the model emits. */
trait LlmEnum {
  def apiValue: String
}

/**
 * Companion-object mixin for an [[LlmEnum]] enum — the sanctioned (and lowest-boilerplate) way to define an LLM-facing
 * closed-set enum. From the enum's own `enumValues` + `apiValue` it provides, for free:
 *   - `fromString`, a case-insensitive parser that REJECTS out-of-set values;
 *   - the circe `Encoder`/`Decoder` (the decoder rejects out-of-set values); and
 *   - the Tapir `Schema` that publishes the closed set as a JSON-schema `enum` (the CONSTRAIN layer).
 *
 * Because the constrained `Schema[E]` lives in the companion, every `StructuredCodec` that embeds the enum
 * automatically tells the model the allowed values — the closed-set guarantee is by construction and can't be
 * forgotten. Defining an LLM-facing enum without this mixin means re-implementing all of the above by hand.
 *
 * Usage: `enum Foo(val apiValue: String) extends LlmEnum { ... }` + `object Foo extends LlmEnumCompanion[Foo] {
 * protected def enumValues = Foo.values; protected def label = "Foo" }`.
 */
trait LlmEnumCompanion[E <: LlmEnum] {

  /** The enum's compiler-generated `values`. */
  protected def enumValues: Array[E]

  /** Type name, used in parse-error messages. */
  protected def label: String

  /** Extra accepted input spellings beyond the case names + apiValues (default none). Keys are matched upper-cased. */
  protected def aliases: Map[String, E] = Map.empty

  private lazy val byKey: Map[String, E] =
    enumValues.iterator.map(e => e.apiValue.toUpperCase -> e).toMap ++
      enumValues.iterator.map(e => e.toString.toUpperCase -> e).toMap ++
      aliases.map { case (k, v) => k.toUpperCase -> v }

  final def fromString(value: String): Either[String, E] =
    byKey
      .get(value.toUpperCase)
      .toRight(s"Unrecognized $label: '$value'. Valid values: ${enumValues.map(_.apiValue).mkString(", ")}")

  given encoder: Encoder[E] = Encoder.encodeString.contramap(_.apiValue)
  given decoder: Decoder[E] = Decoder.decodeString.emap(fromString)

  given schema: Schema[E] =
    Schema.string[E].validate(Validator.enumeration(enumValues.toList, (e: E) => Some(e.apiValue)))

}
