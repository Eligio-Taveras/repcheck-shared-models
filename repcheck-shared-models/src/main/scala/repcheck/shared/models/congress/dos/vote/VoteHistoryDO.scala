package repcheck.shared.models.congress.dos.vote

import java.time.Instant
import java.util.UUID

final case class VoteHistoryDO(
  historyId: UUID,
  voteId: String,
  congress: Int,
  chamber: String,
  rollNumber: Int,
  sessionNumber: Option[Int],
  billId: Option[String],
  question: Option[String],
  voteType: Option[String],
  result: Option[String],
  voteDate: Option[String],
  legislationNumber: Option[String],
  legislationType: Option[String],
  legislationUrl: Option[String],
  sourceDataUrl: Option[String],
  updateDate: Option[String],
  archivedAt: Option[Instant],
)

final case class VoteHistoryPositionDO(
  historyId: UUID,
  memberId: String,
  position: Option[String],
  partyAtVote: Option[String],
  stateAtVote: Option[String],
)
