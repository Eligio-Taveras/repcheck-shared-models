package repcheck.shared.models.user.score

import java.util.UUID

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class ScoreCongressTopicDO(
    userId: UUID,
    memberId: String,
    congress: Int,
    topic: String,
    score: Float
)

object ScoreCongressTopicDO {

  implicit val encoder: Encoder[ScoreCongressTopicDO] = deriveEncoder[ScoreCongressTopicDO]
  implicit val decoder: Decoder[ScoreCongressTopicDO] = deriveDecoder[ScoreCongressTopicDO]

}
