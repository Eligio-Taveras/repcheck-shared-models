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

/**
 * Sub-amendment parent reference. Surfaces when the upstream amendment is itself an amendment to another amendment
 * (mostly Senate floor amendments-to-amendments).
 *
 * Same shape as `AmendedBillDTO` but pointing at an amendment.
 */
final case class AmendedAmendmentDTO(
  congress: Option[Int],
  number: Option[String],
  amendmentType: Option[String],
  description: Option[String],
  purpose: Option[String],
  url: Option[String],
)

object AmendedAmendmentDTO {
  implicit val encoder: Encoder[AmendedAmendmentDTO] = deriveEncoder[AmendedAmendmentDTO]
  implicit val decoder: Decoder[AmendedAmendmentDTO] = deriveDecoder[AmendedAmendmentDTO]
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
  amendedAmendment: Option[AmendedAmendmentDTO],
  chamber: Option[String],
  description: Option[String],
  purpose: Option[String],
  sponsors: Option[List[SponsorDTO]],
  submittedDate: Option[String],
  proposedDate: Option[String],
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

/**
 * One entry under `textVersions` in the response from `/v3/amendment/{congress}/{type}/{number}/text`. Each version
 * carries an optional `type` discriminator (`"Submitted"` | `"Modified"`), optional ISO datetime `date`, and a list of
 * format URLs (PDF + HTML at api.congress.gov / www.congress.gov; rewritten to api.govinfo.gov downstream).
 *
 * `date` is kept as a raw string per the L6 design rule — downstream code parses with `DateParsing.toInstant`.
 */
final case class AmendmentTextItemDTO(
  `type`: Option[String],
  date: Option[String],
  formats: List[AmendmentFormatDTO],
)

object AmendmentTextItemDTO {
  implicit val encoder: Encoder[AmendmentTextItemDTO] = deriveEncoder[AmendmentTextItemDTO]
  implicit val decoder: Decoder[AmendmentTextItemDTO] = deriveDecoder[AmendmentTextItemDTO]
}

/**
 * One format entry under an `AmendmentTextItemDTO.formats` list. `type` is `"PDF"` or `"HTML"`; `url` points at a
 * www.congress.gov CREC URL that the amendment-text-pipeline rewrites to api.govinfo.gov before download.
 */
final case class AmendmentFormatDTO(
  `type`: String,
  url: String,
)

object AmendmentFormatDTO {
  implicit val encoder: Encoder[AmendmentFormatDTO] = deriveEncoder[AmendmentFormatDTO]
  implicit val decoder: Decoder[AmendmentFormatDTO] = deriveDecoder[AmendmentFormatDTO]
}

/**
 * Response envelope for `/v3/amendment/{congress}/{type}/{number}/text`. Shape: `{ "textVersions": [...], "pagination":
 * {...} }`.
 */
final case class AmendmentTextResponseDTO(
  textVersions: List[AmendmentTextItemDTO],
  pagination: Option[PaginationInfoDTO],
)

object AmendmentTextResponseDTO {
  implicit val encoder: Encoder[AmendmentTextResponseDTO] = deriveEncoder[AmendmentTextResponseDTO]
  implicit val decoder: Decoder[AmendmentTextResponseDTO] = deriveDecoder[AmendmentTextResponseDTO]
}
