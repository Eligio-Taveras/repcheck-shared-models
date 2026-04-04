package repcheck.shared.models.user.score

import java.util.UUID

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class ScoreHistoryCongressDO(
    scoreId: UUID,
    congress: Int,
    overallScore: Float
)

object ScoreHistoryCongressDO {

  implicit val encoder: Encoder[ScoreHistoryCongressDO] = deriveEncoder[ScoreHistoryCongressDO]
  implicit val decoder: Decoder[ScoreHistoryCongressDO] = deriveDecoder[ScoreHistoryCongressDO]

}
