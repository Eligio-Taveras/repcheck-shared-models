package repcheck.shared.models.congress.dos.committee

import java.time.{Instant, LocalDate}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.committee.{CommitteePosition, CommitteeSide, CommitteeType}
import repcheck.shared.models.congress.common.Chamber
import repcheck.shared.models.placeholder.HasPlaceholder

final case class CommitteeDO(
  committeeId: Long,
  naturalKey: String,
  name: String,
  chamber: Option[Chamber],
  committeeType: Option[CommitteeType],
  parentCommitteeId: Option[Long],
  isCurrent: Option[Boolean],
  updateDate: Option[Instant],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object CommitteeDO {
  implicit val encoder: Encoder[CommitteeDO] = deriveEncoder[CommitteeDO]
  implicit val decoder: Decoder[CommitteeDO] = deriveDecoder[CommitteeDO]

  implicit val hasPlaceholder: HasPlaceholder[CommitteeDO] = new HasPlaceholder[CommitteeDO] {
    def placeholder(naturalKey: String): CommitteeDO =
      CommitteeDO(
        committeeId = 0L,
        naturalKey = naturalKey,
        name = "",
        chamber = None,
        committeeType = None,
        parentCommitteeId = None,
        isCurrent = None,
        updateDate = None,
        createdAt = None,
        updatedAt = None,
      )
  }

}

final case class CommitteeMemberDO(
  committeeId: Long,
  memberId: Long,
  position: Option[CommitteePosition],
  side: Option[CommitteeSide],
  rank: Option[Int],
  beginDate: Option[LocalDate],
  endDate: Option[LocalDate],
  congress: Option[Int],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object CommitteeMemberDO {
  import repcheck.shared.models.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

  implicit val encoder: Encoder[CommitteeMemberDO] = deriveEncoder[CommitteeMemberDO]
  implicit val decoder: Decoder[CommitteeMemberDO] = deriveDecoder[CommitteeMemberDO]
}

final case class BillCommitteeReferralDO(
  billId: Long,
  committeeId: Long,
  referralDate: Option[LocalDate],
  reportDate: Option[LocalDate],
  activity: Option[String],
  createdAt: Option[Instant],
)

object BillCommitteeReferralDO {
  import repcheck.shared.models.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

  implicit val encoder: Encoder[BillCommitteeReferralDO] = deriveEncoder[BillCommitteeReferralDO]
  implicit val decoder: Decoder[BillCommitteeReferralDO] = deriveDecoder[BillCommitteeReferralDO]
}
