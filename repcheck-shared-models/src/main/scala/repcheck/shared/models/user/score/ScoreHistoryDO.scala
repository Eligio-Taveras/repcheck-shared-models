package repcheck.shared.models.user.score

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class ScoreHistoryDO(
  scoreId: UUID,
  userId: UUID,
  memberId: String,
  computedAt: Option[Instant],
  aggregateScore: Float,
  triggerEvent: Option[String],
  reasoning: Option[String],
)

object ScoreHistoryDO {

  implicit val encoder: Encoder[ScoreHistoryDO] = deriveEncoder[ScoreHistoryDO]
  implicit val decoder: Decoder[ScoreHistoryDO] = deriveDecoder[ScoreHistoryDO]

}
