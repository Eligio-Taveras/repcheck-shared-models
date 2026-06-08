package repcheck.shared.models.llm.agentic

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.llm.tool.{ToolCall, ToolResult}

/**
 * One iteration of the agentic loop: the tool calls the model requested and the results returned for them. The sequence
 * of turns is the persisted transcript (audit + §10c record/replay).
 */
final case class Turn(index: Int, toolCalls: List[ToolCall], toolResults: List[ToolResult])

object Turn {
  given Encoder[Turn] = deriveEncoder[Turn]
  given Decoder[Turn] = deriveDecoder[Turn]
}
