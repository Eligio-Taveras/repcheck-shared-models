package repcheck.shared.models.llm.tool

import io.circe.Json

/**
 * A tool the agentic loop can call, typed on both sides. Arguments decode to a typed `In` (or a [[ToolInputError]] fed
 * back to the model), `execute` runs in `F`, and the typed `Out` encodes to JSON conforming to `spec.resultSchema` — so
 * neither the call nor the result is ever a free-form blob. Implementations live in the tool layer (D19).
 */
trait LlmTool[F[_]] {
  type In
  type Out

  def spec: ToolSpec
  def decode(arguments: Json): Either[ToolInputError, In]
  def execute(in: In): F[Out]
  def encodeResult(out: Out): Json
}
