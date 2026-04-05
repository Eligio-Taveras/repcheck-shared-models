package repcheck.shared.models.congress.dto.committee

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class SenatorCommitteeAssignmentXmlDTO(
  committeeCode: String,
  committeeName: String,
  position: String,
)

object SenatorCommitteeAssignmentXmlDTO {

  implicit val encoder: Encoder[SenatorCommitteeAssignmentXmlDTO] =
    deriveEncoder[SenatorCommitteeAssignmentXmlDTO]

  implicit val decoder: Decoder[SenatorCommitteeAssignmentXmlDTO] =
    deriveDecoder[SenatorCommitteeAssignmentXmlDTO]

}

final case class SenatorCommitteeDataXmlDTO(
  lisMemberId: String,
  bioguideId: String,
  firstName: String,
  lastName: String,
  party: String,
  state: String,
  stateRank: Option[String],
  office: Option[String],
  leadershipPosition: Option[String],
  committees: List[SenatorCommitteeAssignmentXmlDTO],
)

object SenatorCommitteeDataXmlDTO {

  implicit val encoder: Encoder[SenatorCommitteeDataXmlDTO] =
    deriveEncoder[SenatorCommitteeDataXmlDTO]

  implicit val decoder: Decoder[SenatorCommitteeDataXmlDTO] =
    deriveDecoder[SenatorCommitteeDataXmlDTO]

}

final case class HouseCommitteeAssignmentXmlDTO(
  committeeCode: String,
  committeeName: String,
  rank: Option[Int],
  side: String,
)

object HouseCommitteeAssignmentXmlDTO {

  implicit val encoder: Encoder[HouseCommitteeAssignmentXmlDTO] =
    deriveEncoder[HouseCommitteeAssignmentXmlDTO]

  implicit val decoder: Decoder[HouseCommitteeAssignmentXmlDTO] =
    deriveDecoder[HouseCommitteeAssignmentXmlDTO]

}

final case class HouseMemberDataXmlDTO(
  bioguideId: String,
  firstName: String,
  lastName: String,
  party: String,
  state: String,
  district: Option[Int],
  committees: List[HouseCommitteeAssignmentXmlDTO],
)

object HouseMemberDataXmlDTO {

  implicit val encoder: Encoder[HouseMemberDataXmlDTO] =
    deriveEncoder[HouseMemberDataXmlDTO]

  implicit val decoder: Decoder[HouseMemberDataXmlDTO] =
    deriveDecoder[HouseMemberDataXmlDTO]

}

final case class CommitteeActivityDTO(
  name: String,
  date: Option[String],
)

object CommitteeActivityDTO {
  implicit val encoder: Encoder[CommitteeActivityDTO] = deriveEncoder[CommitteeActivityDTO]
  implicit val decoder: Decoder[CommitteeActivityDTO] = deriveDecoder[CommitteeActivityDTO]
}

final case class BillCommitteeReferralDTO(
  committeeCode: String,
  committeeName: String,
  chamber: Option[String],
  activities: List[CommitteeActivityDTO],
)

object BillCommitteeReferralDTO {

  implicit val encoder: Encoder[BillCommitteeReferralDTO] =
    deriveEncoder[BillCommitteeReferralDTO]

  implicit val decoder: Decoder[BillCommitteeReferralDTO] =
    deriveDecoder[BillCommitteeReferralDTO]

}

final case class PaginationCountDTO(
  count: Option[Int],
  url: Option[String],
)

object PaginationCountDTO {
  implicit val encoder: Encoder[PaginationCountDTO] = deriveEncoder[PaginationCountDTO]
  implicit val decoder: Decoder[PaginationCountDTO] = deriveDecoder[PaginationCountDTO]
}

final case class CommitteeListItemDTO(
  chamber: Option[String],
  committeeTypeCode: Option[String],
  name: String,
  systemCode: String,
  updateDate: Option[String],
  url: Option[String],
  parent: Option[CommitteeListItemDTO],
  subcommittees: Option[List[CommitteeListItemDTO]],
)

object CommitteeListItemDTO {
  implicit val encoder: Encoder[CommitteeListItemDTO] = deriveEncoder[CommitteeListItemDTO]
  implicit val decoder: Decoder[CommitteeListItemDTO] = deriveDecoder[CommitteeListItemDTO]
}

final case class CommitteeDetailDTO(
  systemCode: String,
  `type`: Option[String],
  isCurrent: Option[Boolean],
  history: Option[List[String]],
  bills: Option[PaginationCountDTO],
  reports: Option[PaginationCountDTO],
  subcommittees: Option[List[CommitteeListItemDTO]],
)

object CommitteeDetailDTO {
  implicit val encoder: Encoder[CommitteeDetailDTO] = deriveEncoder[CommitteeDetailDTO]
  implicit val decoder: Decoder[CommitteeDetailDTO] = deriveDecoder[CommitteeDetailDTO]
}
