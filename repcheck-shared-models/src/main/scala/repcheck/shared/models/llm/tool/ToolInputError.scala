package repcheck.shared.models.llm.tool

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * Why a tool call's arguments failed to decode into the tool's typed `In`: which `field` and the `reason`. Fed back to
 * the model as typed feedback so it can re-issue a corrected call.
 */
final case class ToolInputError(field: String, reason: String)

object ToolInputError {
  given Encoder[ToolInputError] = deriveEncoder[ToolInputError]
  given Decoder[ToolInputError] = deriveDecoder[ToolInputError]
}
