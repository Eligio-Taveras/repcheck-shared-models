package repcheck.shared.models.congress.dos.vote

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class VoteHistoryDO(
  id: Long,
  voteId: Long,
  congress: Int,
  chamber: String,
  rollNumber: Int,
  sessionNumber: Option[Int],
  billId: Option[Long],
  question: Option[String],
  voteType: Option[String],
  voteMethod: Option[String],
  result: Option[String],
  voteDate: Option[String],
  legislationNumber: Option[String],
  legislationType: Option[String],
  legislationUrl: Option[String],
  sourceDataUrl: Option[String],
  updateDate: Option[String],
  archivedAt: Option[Instant],
)

object VoteHistoryDO {

  implicit val encoder: Encoder[VoteHistoryDO] = deriveEncoder[VoteHistoryDO]
  implicit val decoder: Decoder[VoteHistoryDO] = deriveDecoder[VoteHistoryDO]

}

final case class VoteHistoryPositionDO(
  historyId: Long,
  memberId: Long,
  position: Option[String],
  partyAtVote: Option[String],
  stateAtVote: Option[String],
)

object VoteHistoryPositionDO {

  implicit val encoder: Encoder[VoteHistoryPositionDO] = deriveEncoder[VoteHistoryPositionDO]
  implicit val decoder: Decoder[VoteHistoryPositionDO] = deriveDecoder[VoteHistoryPositionDO]

}
