package repcheck.shared.models.llm.agentic

import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * The outcome of an agentic run: the typed `output`, how many `iterations` it took, the full `transcript`, and the
 * `correlationId` tying it to logs and the persisted transcript.
 */
final case class AgenticResult[A](output: A, iterations: Int, transcript: List[Turn], correlationId: UUID)

object AgenticResult {
  given encoder[A](using Encoder[A]): Encoder[AgenticResult[A]] = deriveEncoder[AgenticResult[A]]
  given decoder[A](using Decoder[A]): Decoder[AgenticResult[A]] = deriveDecoder[AgenticResult[A]]
}
