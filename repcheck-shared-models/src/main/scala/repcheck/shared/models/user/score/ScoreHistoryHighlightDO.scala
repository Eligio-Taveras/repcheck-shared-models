package repcheck.shared.models.user.score

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class ScoreHistoryHighlightDO(
  scoreId: Long,
  billId: Long,
  topic: String,
  stance: String,
  vote: String,
  alignment: Float,
)

object ScoreHistoryHighlightDO {

  implicit val encoder: Encoder[ScoreHistoryHighlightDO] = deriveEncoder[ScoreHistoryHighlightDO]
  implicit val decoder: Decoder[ScoreHistoryHighlightDO] = deriveDecoder[ScoreHistoryHighlightDO]

}
