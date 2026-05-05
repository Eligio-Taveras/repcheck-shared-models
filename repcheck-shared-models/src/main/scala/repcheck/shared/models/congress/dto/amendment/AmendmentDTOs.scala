package repcheck.shared.models.congress.dto.amendment

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, HCursor, Json}

import repcheck.shared.models.congress.dto.bill.{LatestActionDTO, SponsorDTO}
import repcheck.shared.models.congress.dto.common.{PagedObject, PaginationInfoDTO, ResourceLinkDTO}

private object AmendmentDTOsDecoders {

  /**
   * Decode an optional string from JSON field `type`, falling back to a Scala-named field when `type` is absent.
   * `Option[String]` decoding always succeeds with `None` for missing keys, so `.orElse` won't fire on absence — we use
   * `flatMap` to distinguish "field absent / null" from "field present with a value".
   */
  def decodeTypeOrAlias(c: HCursor, alias: String): Decoder.Result[Option[String]] =
    c.downField("type").as[Option[String]].flatMap {
      case some @ Some(_) => Right(some)
      case None           => c.downField(alias).as[Option[String]]
    }

}

/**
 * `amendedBill` sub-object on `/v3/amendment/{c}/{t}/{n}`. Live API returns `number` as a string (e.g. `"3684"`) and
 * uses JSON field `type` for `billType` — custom decoder maps `type` -> `billType` (mirrors
 * `BillReferenceDTO.decoder`).
 */
final case class AmendedBillDTO(
  congress: Option[Int],
  number: Option[String],
  originChamber: Option[String],
  originChamberCode: Option[String],
  title: Option[String],
  billType: Option[String],
  url: Option[String],
  updateDateIncludingText: Option[String],
)

object AmendedBillDTO {

  implicit val encoder: Encoder[AmendedBillDTO] = Encoder.instance { b =>
    val fields = List(
      b.congress.map(v => "congress" -> Json.fromInt(v)),
      b.number.map(v => "number" -> Json.fromString(v)),
      b.originChamber.map(v => "originChamber" -> Json.fromString(v)),
      b.originChamberCode.map(v => "originChamberCode" -> Json.fromString(v)),
      b.title.map(v => "title" -> Json.fromString(v)),
      b.billType.map(v => "type" -> Json.fromString(v)),
      b.url.map(v => "url" -> Json.fromString(v)),
      b.updateDateIncludingText.map(v => "updateDateIncludingText" -> Json.fromString(v)),
    ).flatten
    Json.obj(fields*)
  }

  implicit val decoder: Decoder[AmendedBillDTO] = (c: HCursor) =>
    for {
      congress                <- c.downField("congress").as[Option[Int]]
      number                  <- c.downField("number").as[Option[String]]
      originChamber           <- c.downField("originChamber").as[Option[String]]
      originChamberCode       <- c.downField("originChamberCode").as[Option[String]]
      title                   <- c.downField("title").as[Option[String]]
      billType                <- AmendmentDTOsDecoders.decodeTypeOrAlias(c, "billType")
      url                     <- c.downField("url").as[Option[String]]
      updateDateIncludingText <- c.downField("updateDateIncludingText").as[Option[String]]
    } yield AmendedBillDTO(
      congress = congress,
      number = number,
      originChamber = originChamber,
      originChamberCode = originChamberCode,
      title = title,
      billType = billType,
      url = url,
      updateDateIncludingText = updateDateIncludingText,
    )

}

/**
 * Sub-amendment parent reference. Surfaces when the upstream amendment is itself an amendment to another amendment
 * (mostly Senate floor amendments-to-amendments).
 *
 * Live API returns JSON field `type` for `amendmentType` — custom decoder mirrors `BillReferenceDTO.decoder`. Real
 * responses include `updateDate`; no `description` field exists.
 */
final case class AmendedAmendmentDTO(
  congress: Option[Int],
  number: Option[String],
  amendmentType: Option[String],
  purpose: Option[String],
  updateDate: Option[String],
  url: Option[String],
)

object AmendedAmendmentDTO {

  implicit val encoder: Encoder[AmendedAmendmentDTO] = Encoder.instance { a =>
    val fields = List(
      a.congress.map(v => "congress" -> Json.fromInt(v)),
      a.number.map(v => "number" -> Json.fromString(v)),
      a.amendmentType.map(v => "type" -> Json.fromString(v)),
      a.purpose.map(v => "purpose" -> Json.fromString(v)),
      a.updateDate.map(v => "updateDate" -> Json.fromString(v)),
      a.url.map(v => "url" -> Json.fromString(v)),
    ).flatten
    Json.obj(fields*)
  }

  implicit val decoder: Decoder[AmendedAmendmentDTO] = (c: HCursor) =>
    for {
      congress      <- c.downField("congress").as[Option[Int]]
      number        <- c.downField("number").as[Option[String]]
      amendmentType <- AmendmentDTOsDecoders.decodeTypeOrAlias(c, "amendmentType")
      purpose       <- c.downField("purpose").as[Option[String]]
      updateDate    <- c.downField("updateDate").as[Option[String]]
      url           <- c.downField("url").as[Option[String]]
    } yield AmendedAmendmentDTO(
      congress = congress,
      number = number,
      amendmentType = amendmentType,
      purpose = purpose,
      updateDate = updateDate,
      url = url,
    )

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

  implicit val encoder: Encoder[AmendmentListItemDTO] = Encoder.instance { a =>
    val fields = List(
      Some("congress" -> Json.fromInt(a.congress)),
      Some("number"   -> Json.fromString(a.number)),
      a.amendmentType.map(v => "type" -> Json.fromString(v)),
      a.description.map(v => "description" -> Json.fromString(v)),
      a.latestAction.map(v => "latestAction" -> LatestActionDTO.encoder.apply(v)),
      a.updateDate.map(v => "updateDate" -> Json.fromString(v)),
      a.url.map(v => "url" -> Json.fromString(v)),
    ).flatten
    Json.obj(fields*)
  }

  implicit val decoder: Decoder[AmendmentListItemDTO] = (c: HCursor) =>
    for {
      congress      <- c.downField("congress").as[Int]
      number        <- c.downField("number").as[String]
      amendmentType <- AmendmentDTOsDecoders.decodeTypeOrAlias(c, "amendmentType")
      description   <- c.downField("description").as[Option[String]]
      latestAction  <- c.downField("latestAction").as[Option[LatestActionDTO]]
      updateDate    <- c.downField("updateDate").as[Option[String]]
      url           <- c.downField("url").as[Option[String]]
    } yield AmendmentListItemDTO(
      congress = congress,
      number = number,
      amendmentType = amendmentType,
      description = description,
      latestAction = latestAction,
      updateDate = updateDate,
      url = url,
    )

}

/**
 * Detail response for `/v3/amendment/{c}/{t}/{n}`. JSON field `type` decodes into `amendmentType` (custom decoder).
 *
 * `actions`, `textVersions`, `cosponsors`, `amendmentsToAmendment` are returned as link sub-objects of shape `{count,
 * url}` pointing to dedicated endpoints — NOT inline data. They decode into [[ResourceLinkDTO]]. The
 * amendments-pipeline calls those URLs separately to materialize the data.
 */
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
  actions: Option[ResourceLinkDTO],
  textVersions: Option[ResourceLinkDTO],
  cosponsors: Option[ResourceLinkDTO],
  amendmentsToAmendment: Option[ResourceLinkDTO],
)

object AmendmentDetailDTO {

  implicit val encoder: Encoder[AmendmentDetailDTO] = Encoder.instance { d =>
    val fields = List(
      Some("congress" -> Json.fromInt(d.congress)),
      Some("number"   -> Json.fromString(d.number)),
      d.amendmentType.map(v => "type" -> Json.fromString(v)),
      d.amendedBill.map(v => "amendedBill" -> AmendedBillDTO.encoder.apply(v)),
      d.amendedAmendment.map(v => "amendedAmendment" -> AmendedAmendmentDTO.encoder.apply(v)),
      d.chamber.map(v => "chamber" -> Json.fromString(v)),
      d.description.map(v => "description" -> Json.fromString(v)),
      d.purpose.map(v => "purpose" -> Json.fromString(v)),
      d.sponsors.map(v => "sponsors" -> Encoder.encodeList(using SponsorDTO.encoder).apply(v)),
      d.submittedDate.map(v => "submittedDate" -> Json.fromString(v)),
      d.proposedDate.map(v => "proposedDate" -> Json.fromString(v)),
      d.latestAction.map(v => "latestAction" -> LatestActionDTO.encoder.apply(v)),
      d.updateDate.map(v => "updateDate" -> Json.fromString(v)),
      d.actions.map(v => "actions" -> ResourceLinkDTO.encoder.apply(v)),
      d.textVersions.map(v => "textVersions" -> ResourceLinkDTO.encoder.apply(v)),
      d.cosponsors.map(v => "cosponsors" -> ResourceLinkDTO.encoder.apply(v)),
      d.amendmentsToAmendment.map(v => "amendmentsToAmendment" -> ResourceLinkDTO.encoder.apply(v)),
    ).flatten
    Json.obj(fields*)
  }

  /**
   * Decode an optional sub-object; if the JSON shape doesn't match (e.g. inline list where a link object is expected),
   * fall back to `None` rather than failing the whole detail decode.
   */
  private def optionalOrNone[A: Decoder](c: HCursor, field: String): Decoder.Result[Option[A]] =
    c.downField(field).as[Option[A]].orElse(Right(None))

  implicit val decoder: Decoder[AmendmentDetailDTO] = (c: HCursor) =>
    for {
      congress         <- c.downField("congress").as[Int]
      number           <- c.downField("number").as[String]
      amendmentType    <- AmendmentDTOsDecoders.decodeTypeOrAlias(c, "amendmentType")
      amendedBill      <- c.downField("amendedBill").as[Option[AmendedBillDTO]]
      amendedAmendment <- c.downField("amendedAmendment").as[Option[AmendedAmendmentDTO]]
      chamber          <- c.downField("chamber").as[Option[String]]
      description      <- c.downField("description").as[Option[String]]
      purpose          <- c.downField("purpose").as[Option[String]]
      sponsors         <- c.downField("sponsors").as[Option[List[SponsorDTO]]]
      submittedDate    <- c.downField("submittedDate").as[Option[String]]
      proposedDate     <- c.downField("proposedDate").as[Option[String]]
      latestAction     <- c.downField("latestAction").as[Option[LatestActionDTO]]
      updateDate       <- c.downField("updateDate").as[Option[String]]
      // Real responses ship these as { count, url } link objects, not inline arrays. Fall back to None on any mismatch
      // to keep the rest of the detail decode robust if Congress.gov ever inlines them.
      actions               <- optionalOrNone[ResourceLinkDTO](c, "actions")
      textVersions          <- optionalOrNone[ResourceLinkDTO](c, "textVersions")
      cosponsors            <- optionalOrNone[ResourceLinkDTO](c, "cosponsors")
      amendmentsToAmendment <- optionalOrNone[ResourceLinkDTO](c, "amendmentsToAmendment")
    } yield AmendmentDetailDTO(
      congress = congress,
      number = number,
      amendmentType = amendmentType,
      amendedBill = amendedBill,
      amendedAmendment = amendedAmendment,
      chamber = chamber,
      description = description,
      purpose = purpose,
      sponsors = sponsors,
      submittedDate = submittedDate,
      proposedDate = proposedDate,
      latestAction = latestAction,
      updateDate = updateDate,
      actions = actions,
      textVersions = textVersions,
      cosponsors = cosponsors,
      amendmentsToAmendment = amendmentsToAmendment,
    )

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
