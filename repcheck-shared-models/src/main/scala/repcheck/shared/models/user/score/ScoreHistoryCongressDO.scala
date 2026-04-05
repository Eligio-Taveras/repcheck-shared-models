package repcheck.shared.models.user.score

import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class ScoreHistoryCongressDO(
  scoreId: UUID,
  congress: Int,
  overallScore: Float,
  billsConsidered: Option[Int],
  votesAnalyzed: Option[Int],
)

object ScoreHistoryCongressDO {

  implicit val encoder: Encoder[ScoreHistoryCongressDO] = deriveEncoder[ScoreHistoryCongressDO]
  implicit val decoder: Decoder[ScoreHistoryCongressDO] = deriveDecoder[ScoreHistoryCongressDO]

}
