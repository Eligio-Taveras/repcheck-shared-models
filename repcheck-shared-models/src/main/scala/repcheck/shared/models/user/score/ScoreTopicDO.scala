package repcheck.shared.models.user.score

import java.util.UUID

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class ScoreTopicDO(
    userId: UUID,
    memberId: String,
    topic: String,
    score: Float,
    topBillId: Option[String]
)

object ScoreTopicDO {

  implicit val encoder: Encoder[ScoreTopicDO] = deriveEncoder[ScoreTopicDO]
  implicit val decoder: Decoder[ScoreTopicDO] = deriveDecoder[ScoreTopicDO]

}
