package repcheck.shared.models.user

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class StanceMaterializationStatusDO(
  billId: String,
  hasVotes: Boolean,
  hasAnalysis: Boolean,
  allPassesCompleted: Boolean,
  votesUpdatedAt: Option[Instant],
  analysisCompletedAt: Option[Instant],
  stancesMaterializedAt: Option[Instant],
  lastScoringRunAt: Option[Instant],
)

object StanceMaterializationStatusDO {

  implicit val encoder: Encoder[StanceMaterializationStatusDO] = deriveEncoder[StanceMaterializationStatusDO]
  implicit val decoder: Decoder[StanceMaterializationStatusDO] = deriveDecoder[StanceMaterializationStatusDO]

}
