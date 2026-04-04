package repcheck.shared.models.congress.dos.member

import java.util.UUID

final case class MemberPartyHistoryDO(
    partyHistoryId: UUID,
    memberId: String,
    partyName: Option[String],
    partyAbbreviation: Option[String],
    startYear: Option[Int]
)
