package repcheck.shared.models.user.score

import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class ScoreTopicDO(
  userId: UUID,
  memberId: Long,
  topic: String,
  score: Float,
)

object ScoreTopicDO {

  implicit val encoder: Encoder[ScoreTopicDO] = deriveEncoder[ScoreTopicDO]
  implicit val decoder: Decoder[ScoreTopicDO] = deriveDecoder[ScoreTopicDO]

}
