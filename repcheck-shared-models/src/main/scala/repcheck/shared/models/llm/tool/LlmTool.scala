package repcheck.shared.models.llm.tool

import cats.Applicative
import cats.syntax.all._

import io.circe.Json

/**
 * A tool the agentic loop can call, typed on both sides via the `In`/`Out` type parameters. Arguments decode to `In`
 * (or a [[ToolInputError]] fed back to the model), `execute` runs in `F`, and `Out` encodes to JSON conforming to
 * `spec.resultSchema` — so neither the call nor the result is ever a free-form blob. [[invoke]] runs the full decode →
 * execute → encode round trip with `In`/`Out` staying internal, so a caller holding a heterogeneous `LlmTool[F, ?, ?]`
 * (a registry or the runner) dispatches json-in/json-out without touching the type parameters. Implementations live in
 * the tool layer (D19).
 */
trait LlmTool[F[_], In, Out] {

  def spec: ToolSpec
  def decode(arguments: Json): Either[ToolInputError, In]
  def execute(in: In): F[Out]
  def encodeResult(out: Out): Json

  /**
   * Full json→json invocation; `In`/`Out` never escape. A decode failure surfaces as `Left` for the caller to relay.
   */
  def invoke(arguments: Json)(using F: Applicative[F]): F[Either[ToolInputError, Json]] =
    decode(arguments) match {
      case Left(err) => F.pure(Left(err))
      case Right(in) => execute(in).map(out => Right(encodeResult(out)))
    }

}
