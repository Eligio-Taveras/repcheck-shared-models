package repcheck.shared.models.congress.dos.vote

import java.time.{Instant, LocalDate}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.amendment.AmendmentType
import repcheck.shared.models.congress.common.{BillType, Chamber, LegislationKind}
import repcheck.shared.models.congress.vote.{VoteMethod, VoteType}
import repcheck.shared.models.placeholder.HasPlaceholder

/**
 * Vote DO. Storage row for a roll-call vote.
 *
 * Legislation discrimination is split across three columns:
 *   - `legislationType: LegislationKind` — the discriminator (BILL / AMENDMENT)
 *   - `billType: BillType` — populated iff `legislationType == BILL`
 *   - `amendmentType: AmendmentType` — populated iff `legislationType == AMENDMENT`
 *
 * Procedural votes (no underlying legislation) leave all three None. Field order in this case class MUST match the
 * SELECT column order used by the votes repository — Doobie's auto-derived `Read` is positional, not name-based, so a
 * mismatch with the parallel `repcheck-db-migrations` schema (which lists columns `bill_type, amendment_type,
 * legislation_type` in this section) causes silent type errors at runtime.
 */
final case class VoteDO(
  voteId: Long,
  naturalKey: String,
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
  legislationType: Option[LegislationKind],
  billType: Option[BillType],
  amendmentType: Option[AmendmentType],
  legislationUrl: Option[String],
  sourceDataUrl: Option[String],
  updateDate: Option[Instant],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object VoteDO {

  import repcheck.shared.models.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

  implicit val encoder: Encoder[VoteDO] = deriveEncoder[VoteDO]
  implicit val decoder: Decoder[VoteDO] = deriveDecoder[VoteDO]

  implicit val hasPlaceholder: HasPlaceholder[VoteDO] = new HasPlaceholder[VoteDO] {
    def placeholder(naturalKey: String): VoteDO =
      VoteDO(
        voteId = 0L,
        naturalKey = naturalKey,
        congress = 0,
        chamber = Chamber.House,
        rollNumber = 0,
        sessionNumber = None,
        billId = None,
        question = None,
        voteType = None,
        voteMethod = None,
        result = None,
        voteDate = None,
        legislationNumber = None,
        legislationType = None,
        billType = None,
        amendmentType = None,
        legislationUrl = None,
        sourceDataUrl = None,
        updateDate = None,
        createdAt = None,
        updatedAt = None,
      )
  }

}
