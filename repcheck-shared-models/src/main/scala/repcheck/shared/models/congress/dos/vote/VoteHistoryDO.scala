package repcheck.shared.models.congress.dos.vote

import java.time.{Instant, LocalDate}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.common.{BillType, Chamber, Party, UsState}
import repcheck.shared.models.congress.vote.{VoteCast, VoteMethod, VoteType}

final case class VoteHistoryDO(
  id: Long,
  voteId: Long,
  congress: Int,
  chamber: Chamber,
  rollNumber: Int,
  sessionNumber: Option[Int],
  billId: Option[Long],
  question: Option[String],
  voteType: Option[VoteType],
  voteMethod: Option[VoteMethod],
  result: Option[String],
  voteDate: Option[LocalDate],
  legislationNumber: Option[String],
  legislationType: Option[BillType],
  legislationUrl: Option[String],
  sourceDataUrl: Option[String],
  updateDate: Option[Instant],
  archivedAt: Option[Instant],
)

object VoteHistoryDO {

  import repcheck.shared.models.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

  implicit val encoder: Encoder[VoteHistoryDO] = deriveEncoder[VoteHistoryDO]
  implicit val decoder: Decoder[VoteHistoryDO] = deriveDecoder[VoteHistoryDO]

}

final case class VoteHistoryPositionDO(
  historyId: Long,
  memberId: Long,
  position: Option[VoteCast],
  partyAtVote: Option[Party],
  stateAtVote: Option[UsState],
)

object VoteHistoryPositionDO {

  implicit val encoder: Encoder[VoteHistoryPositionDO] = deriveEncoder[VoteHistoryPositionDO]
  implicit val decoder: Decoder[VoteHistoryPositionDO] = deriveDecoder[VoteHistoryPositionDO]

}
