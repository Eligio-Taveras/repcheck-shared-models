package repcheck.shared.models.user.score

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class ScoreHistoryCongressTopicDO(
  scoreId: Long,
  congress: Int,
  topic: String,
  score: Float,
)

object ScoreHistoryCongressTopicDO {

  implicit val encoder: Encoder[ScoreHistoryCongressTopicDO] = deriveEncoder[ScoreHistoryCongressTopicDO]
  implicit val decoder: Decoder[ScoreHistoryCongressTopicDO] = deriveDecoder[ScoreHistoryCongressTopicDO]

}
