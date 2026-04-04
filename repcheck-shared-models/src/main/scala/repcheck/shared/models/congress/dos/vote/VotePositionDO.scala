package repcheck.shared.models.congress.dos.vote

import java.time.Instant

final case class VotePositionDO(
    voteId: String,
    memberId: String,
    position: Option[String],
    partyAtVote: Option[String],
    stateAtVote: Option[String],
    createdAt: Option[Instant]
)
