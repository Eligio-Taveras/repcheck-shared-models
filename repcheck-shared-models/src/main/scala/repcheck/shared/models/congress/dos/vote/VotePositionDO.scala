package repcheck.shared.models.congress.dos.vote

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class VotePositionDO(
  voteId: Long,
  memberId: Long,
  position: Option[String],
  partyAtVote: Option[String],
  stateAtVote: Option[String],
  createdAt: Option[Instant],
)

object VotePositionDO {

  implicit val encoder: Encoder[VotePositionDO] = deriveEncoder[VotePositionDO]
  implicit val decoder: Decoder[VotePositionDO] = deriveDecoder[VotePositionDO]

}
