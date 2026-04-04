package repcheck.shared.models.user.score

import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class ScoreHistoryHighlightDO(
  scoreId: UUID,
  billId: String,
  topic: String,
  stance: String,
  vote: String,
  alignment: Float,
)

object ScoreHistoryHighlightDO {

  implicit val encoder: Encoder[ScoreHistoryHighlightDO] = deriveEncoder[ScoreHistoryHighlightDO]
  implicit val decoder: Decoder[ScoreHistoryHighlightDO] = deriveDecoder[ScoreHistoryHighlightDO]

}
