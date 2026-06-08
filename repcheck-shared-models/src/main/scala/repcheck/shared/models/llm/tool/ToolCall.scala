package repcheck.shared.models.llm.tool

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}

/**
 * A tool invocation requested by the model: the tool `name` and its raw JSON `arguments` (validated against the tool's
 * `parametersSchema`, then decoded to the typed `In`). Wire shape — recorded in the transcript.
 */
final case class ToolCall(name: String, arguments: Json)

object ToolCall {
  given Encoder[ToolCall] = deriveEncoder[ToolCall]
  given Decoder[ToolCall] = deriveDecoder[ToolCall]
}
