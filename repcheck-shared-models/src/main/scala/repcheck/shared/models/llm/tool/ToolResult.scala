package repcheck.shared.models.llm.tool

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}

/**
 * The result handed back to the model after a tool runs: the tool `name`, its structured `content` (the typed `Out`
 * encoded, conforming to the tool's `resultSchema` — never a free-form blob), and whether it `isError`. Wire shape —
 * recorded in the transcript.
 */
final case class ToolResult(name: String, content: Json, isError: Boolean)

object ToolResult {
  given Encoder[ToolResult] = deriveEncoder[ToolResult]
  given Decoder[ToolResult] = deriveDecoder[ToolResult]
}
