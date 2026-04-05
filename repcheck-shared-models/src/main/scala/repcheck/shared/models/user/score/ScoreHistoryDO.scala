package repcheck.shared.models.user.score

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.codecs.VectorCodec._

final case class ScoreHistoryDO(
  scoreId: UUID,
  userId: UUID,
  memberId: String,
  computedAt: Option[Instant],
  aggregateScore: Float,
  status: Option[String],
  triggerEvent: Option[String],
  reasoning: Option[String],
  reasoningEmbedding: Option[Array[Float]],
)

object ScoreHistoryDO {

  implicit val encoder: Encoder[ScoreHistoryDO] = deriveEncoder[ScoreHistoryDO]
  implicit val decoder: Decoder[ScoreHistoryDO] = deriveDecoder[ScoreHistoryDO]

}
