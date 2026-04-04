package repcheck.shared.models.user.score

import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class ScoreHistoryCongressTopicDO(
  scoreId: UUID,
  congress: Int,
  topic: String,
  score: Float,
)

object ScoreHistoryCongressTopicDO {

  implicit val encoder: Encoder[ScoreHistoryCongressTopicDO] = deriveEncoder[ScoreHistoryCongressTopicDO]
  implicit val decoder: Decoder[ScoreHistoryCongressTopicDO] = deriveDecoder[ScoreHistoryCongressTopicDO]

}
