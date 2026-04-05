package repcheck.shared.models.congress.dos.committee

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class CommitteeDO(
  committeeCode: String,
  name: String,
  chamber: Option[String],
  committeeType: Option[String],
  parentCommitteeCode: Option[String],
  isCurrent: Option[Boolean],
  updateDate: Option[String],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object CommitteeDO {
  implicit val encoder: Encoder[CommitteeDO] = deriveEncoder[CommitteeDO]
  implicit val decoder: Decoder[CommitteeDO] = deriveDecoder[CommitteeDO]
}

final case class CommitteeMemberDO(
  committeeCode: String,
  memberId: String,
  position: Option[String],
  side: Option[String],
  rank: Option[Int],
  beginDate: Option[String],
  endDate: Option[String],
  congress: Option[Int],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object CommitteeMemberDO {
  implicit val encoder: Encoder[CommitteeMemberDO] = deriveEncoder[CommitteeMemberDO]
  implicit val decoder: Decoder[CommitteeMemberDO] = deriveDecoder[CommitteeMemberDO]
}

final case class BillCommitteeReferralDO(
  billId: String,
  committeeCode: String,
  referralDate: Option[String],
  reportDate: Option[String],
  activity: Option[String],
  createdAt: Option[Instant],
)

object BillCommitteeReferralDO {
  implicit val encoder: Encoder[BillCommitteeReferralDO] = deriveEncoder[BillCommitteeReferralDO]
  implicit val decoder: Decoder[BillCommitteeReferralDO] = deriveDecoder[BillCommitteeReferralDO]
}
