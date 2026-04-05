package repcheck.shared.models.congress.dto.amendment

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.dto.bill.{LatestActionDTO, SponsorDTO}
import repcheck.shared.models.congress.dto.common.{PagedObject, PaginationInfoDTO}

final case class AmendedBillDTO(
  congress: Option[Int],
  number: Option[Int],
  originChamber: Option[String],
  originChamberCode: Option[String],
  title: Option[String],
  billType: Option[String],
  url: Option[String],
  updateDateIncludingText: Option[String],
)

object AmendedBillDTO {
  implicit val encoder: Encoder[AmendedBillDTO] = deriveEncoder[AmendedBillDTO]
  implicit val decoder: Decoder[AmendedBillDTO] = deriveDecoder[AmendedBillDTO]
}

final case class AmendmentListItemDTO(
  congress: Int,
  number: String,
  amendmentType: Option[String],
  description: Option[String],
  latestAction: Option[LatestActionDTO],
  updateDate: Option[String],
  url: Option[String],
)

object AmendmentListItemDTO {
  implicit val encoder: Encoder[AmendmentListItemDTO] = deriveEncoder[AmendmentListItemDTO]
  implicit val decoder: Decoder[AmendmentListItemDTO] = deriveDecoder[AmendmentListItemDTO]
}

final case class AmendmentDetailDTO(
  congress: Int,
  number: String,
  amendmentType: Option[String],
  amendedBill: Option[AmendedBillDTO],
  chamber: Option[String],
  description: Option[String],
  purpose: Option[String],
  sponsors: Option[List[SponsorDTO]],
  submittedDate: Option[String],
  latestAction: Option[LatestActionDTO],
  updateDate: Option[String],
  actions: Option[List[LatestActionDTO]],
  textVersions: Option[List[String]],
)

object AmendmentDetailDTO {
  implicit val encoder: Encoder[AmendmentDetailDTO] = deriveEncoder[AmendmentDetailDTO]
  implicit val decoder: Decoder[AmendmentDetailDTO] = deriveDecoder[AmendmentDetailDTO]
}

final case class AmendmentListResponseDTO(
  items: List[AmendmentListItemDTO],
  pagination: Option[PaginationInfoDTO],
) extends PagedObject[AmendmentListItemDTO]

object AmendmentListResponseDTO {
  import cats.Semigroup

  implicit val semigroup: Semigroup[AmendmentListResponseDTO] = Semigroup.instance { (a, b) =>
    AmendmentListResponseDTO(
      items = a.items ++ b.items,
      pagination = b.pagination,
    )
  }

  implicit val encoder: Encoder[AmendmentListResponseDTO] = deriveEncoder[AmendmentListResponseDTO]
  implicit val decoder: Decoder[AmendmentListResponseDTO] = deriveDecoder[AmendmentListResponseDTO]
}
