package repcheck.shared.models.llm.tool

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}

import repcheck.shared.models.llm.codec.StructuredCodec

/**
 * What the model is told about a tool: its `name`/`description`, the JSON Schemas of its input and output, and a
 * codec-encoded example of each. Both schemas and examples are derived from the In/Out [[StructuredCodec]]s (see
 * [[ToolSpec.from]]), so the tool's advertised contract can't drift from the types it actually decodes/encodes.
 */
final case class ToolSpec(
  name: String,
  description: String,
  parametersSchema: Json,
  resultSchema: Json,
  exampleArgs: Json,
  exampleResult: Json,
)

object ToolSpec {
  given Encoder[ToolSpec] = deriveEncoder[ToolSpec]
  given Decoder[ToolSpec] = deriveDecoder[ToolSpec]

  /** Build a spec from the tool's input/output codecs — schemas + examples come straight from the types. */
  def from[In, Out](name: String, description: String)(using
    in: StructuredCodec[In],
    out: StructuredCodec[Out],
  ): ToolSpec =
    ToolSpec(name, description, in.jsonSchema, out.jsonSchema, in.exampleJson, out.exampleJson)

}
