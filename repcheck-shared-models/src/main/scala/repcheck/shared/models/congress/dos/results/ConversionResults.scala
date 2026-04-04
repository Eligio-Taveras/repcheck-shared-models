package repcheck.shared.models.congress.dos.results

import repcheck.shared.models.congress.dos.bill.{BillCosponsorDO, BillDO, BillSubjectDO}
import repcheck.shared.models.congress.dos.member.{MemberDO, MemberPartyHistoryDO, MemberTermDO}
import repcheck.shared.models.congress.dos.vote.{VoteDO, VotePositionDO}

final case class BillConversionResult(
    bill: BillDO,
    cosponsors: List[BillCosponsorDO],
    subjects: List[BillSubjectDO]
)

final case class MemberConversionResult(
    member: MemberDO,
    terms: List[MemberTermDO],
    partyHistory: List[MemberPartyHistoryDO]
)

final case class VoteConversionResult(
    vote: VoteDO,
    positions: List[VotePositionDO]
)
