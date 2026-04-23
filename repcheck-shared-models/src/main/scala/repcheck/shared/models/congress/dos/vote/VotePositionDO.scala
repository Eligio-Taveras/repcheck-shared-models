package repcheck.shared.models.congress.dos.vote

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.common.{Party, UsState}
import repcheck.shared.models.congress.vote.VoteCast

/**
 * A single member's position on a roll-call vote.
 *
 * Dual-identity per migration 023: a row populates EITHER `memberId` (House — bioguide-resolved to `members.id`) OR
 * `lisMemberId` (Senate — `lis_members.id`). The DB `chk_vp_xor_identity` CHECK enforces exactly one is non-NULL; the
 * DO carries the invariant as a caller responsibility.
 *
 *   - `id` — surrogate PK (`vote_positions.id BIGSERIAL`, added in migration 011). Use `0L` for unpersisted rows; the
 *     DB assigns the value on INSERT RETURNING.
 *   - `voteId` — FK to `votes.id`.
 *   - `memberId` — FK to `members.id`; populated for House votes. Nullable as of migration 023.
 *   - `lisMemberId` — FK to `lis_members.id`; populated for Senate votes (both mapped and unmapped senators).
 *   - `voteCastCandidateName` — populated only when `position = VoteCast.Candidate` (officer-election votes where the
 *     member voted for a specific candidate like "Jeffries" or "Johnson (LA)"). Added in migration 025 with a CHECK
 *     constraint tying it to `position = 'Candidate'`; always `None` for every other `position` value.
 *
 * Field order mirrors the canonical SELECT column order used by downstream repositories so the Doobie auto-derived
 * `Read` / `Write` instances align positionally.
 */
final case class VotePositionDO(
  id: Long,
  voteId: Long,
  memberId: Option[Long],
  position: Option[VoteCast],
  partyAtVote: Option[Party],
  stateAtVote: Option[UsState],
  createdAt: Option[Instant],
  lisMemberId: Option[Long],
  // Only populated when `position = Some(VoteCast.Candidate)`. Default `None` matches every non-election vote — the DB
  // `chk_vp_candidate_name` CHECK enforces the invariant from the other side (migration 025).
  voteCastCandidateName: Option[String] = None,
)

object VotePositionDO {

  implicit val encoder: Encoder[VotePositionDO] = deriveEncoder[VotePositionDO]
  implicit val decoder: Decoder[VotePositionDO] = deriveDecoder[VotePositionDO]

}
