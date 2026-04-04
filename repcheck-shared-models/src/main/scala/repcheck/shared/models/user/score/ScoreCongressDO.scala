package repcheck.shared.models.user.score

import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class ScoreCongressDO(
  userId: UUID,
  memberId: String,
  congress: Int,
  overallScore: Float,
  votesCounted: Option[Int],
)

object ScoreCongressDO {

  implicit val encoder: Encoder[ScoreCongressDO] = deriveEncoder[ScoreCongressDO]
  implicit val decoder: Decoder[ScoreCongressDO] = deriveDecoder[ScoreCongressDO]

}
