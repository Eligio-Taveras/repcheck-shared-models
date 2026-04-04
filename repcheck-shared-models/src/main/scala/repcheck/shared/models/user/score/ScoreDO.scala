package repcheck.shared.models.user.score

import java.time.Instant
import java.util.UUID

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class ScoreDO(
    userId: UUID,
    memberId: String,
    aggregateScore: Float,
    computedAt: Option[Instant],
    triggerEvent: Option[String]
)

object ScoreDO {

  implicit val encoder: Encoder[ScoreDO] = deriveEncoder[ScoreDO]
  implicit val decoder: Decoder[ScoreDO] = deriveDecoder[ScoreDO]

}
