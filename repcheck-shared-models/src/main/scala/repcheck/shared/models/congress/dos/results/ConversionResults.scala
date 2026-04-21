package repcheck.shared.models.congress.dos.results

import repcheck.shared.models.congress.common.{Party, UsState}
import repcheck.shared.models.congress.dos.bill.{BillCosponsorDO, BillDO, BillSubjectDO}
import repcheck.shared.models.congress.dos.member.{MemberDO, MemberPartyHistoryDO, MemberTermDO}
import repcheck.shared.models.congress.dos.vote.VoteDO
import repcheck.shared.models.congress.vote.VoteCast

final case class BillConversionResult(
  bill: BillDO,
  cosponsors: List[BillCosponsorDO],
  subjects: List[BillSubjectDO],
)

final case class MemberConversionResult(
  member: MemberDO,
  terms: List[MemberTermDO],
  partyHistory: List[MemberPartyHistoryDO],
)

/**
 * Output of DTO→DO conversion for a single roll-call vote.
 *
 * Carries the partially-populated [[VoteDO]] (with `billId = None` and `voteId = 0L`) plus the two pieces of
 * information the pure conversion layer cannot resolve without DB access:
 *
 *   - `billNaturalKey` — the bill's natural key (e.g., "118-HR-1234") built from the DTO's legislation fields. The
 *     processor looks this up in the bills table (or placeholder-creates) and sets [[VoteDO.billId]] before upsert.
 *     `None` only when the vote is procedural (no associated bill).
 *   - `positions: List[UnresolvedVotePosition]` — one entry per voter with the source identifier (bioguide for House,
 *     lis_member_id for Senate pre-resolution). The processor resolves each to a `members.id` Long (creating
 *     placeholder members when absent) and materializes the final `VotePositionDO` rows with the resolved memberId and
 *     the inserted vote's returned voteId.
 */
final case class VoteConversionResult(
  vote: VoteDO,
  billNaturalKey: Option[String],
  positions: List[UnresolvedVotePosition],
)

/**
 * An intermediate per-position record produced by the pure DTO→DO layer. Carries only the information the DTO has — the
 * source member identifier and the position/party/state fields — leaving `voteId` and `memberId` Long resolution to the
 * processor, which knows the DB-assigned PKs.
 *
 * `memberSource` is `Left(bioguide)` for House votes (Congress.gov API returns `bioguideID` directly) and
 * `Right(lisMemberId)` for Senate votes that haven't been mapped yet (senate.gov XML uses LIS IDs). Today the Senate
 * path pre-resolves via `SenateVoteXmlDTO.toDO(lisMapping)` and emits `Left(bioguide)`; the `Right` variant is reserved
 * for a future refactor (P2.3) where the processor handles LIS→member resolution with placeholder creation.
 */
final case class UnresolvedVotePosition(
  memberSource: Either[String, String],
  voteCast: Option[VoteCast],
  partyAtVote: Option[Party],
  stateAtVote: Option[UsState],
)
