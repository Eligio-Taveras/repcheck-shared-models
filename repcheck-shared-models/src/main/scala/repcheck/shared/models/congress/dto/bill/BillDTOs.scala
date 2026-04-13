package repcheck.shared.models.congress.dto.bill

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, HCursor, Json}

import repcheck.shared.models.congress.dto.common.{PagedObject, PaginationInfoDTO}

final case class LatestActionDTO(
  actionDate: String,
  text: String,
)

object LatestActionDTO {
  implicit val encoder: Encoder[LatestActionDTO] = deriveEncoder[LatestActionDTO]
  implicit val decoder: Decoder[LatestActionDTO] = deriveDecoder[LatestActionDTO]
}

final case class SourceSystemDTO(
  code: Option[Int],
  name: Option[String],
)

object SourceSystemDTO {
  implicit val encoder: Encoder[SourceSystemDTO] = deriveEncoder[SourceSystemDTO]
  implicit val decoder: Decoder[SourceSystemDTO] = deriveDecoder[SourceSystemDTO]
}

final case class SponsorDTO(
  bioguideId: String,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  middleName: Option[String],
  isByRequest: Option[String],
  party: Option[String],
  state: Option[String],
  url: Option[String],
)

object SponsorDTO {
  implicit val encoder: Encoder[SponsorDTO] = deriveEncoder[SponsorDTO]
  implicit val decoder: Decoder[SponsorDTO] = deriveDecoder[SponsorDTO]
}

final case class CoSponsorDTO(
  bioguideId: String,
  district: Option[Int],
  firstName: Option[String],
  fullName: Option[String],
  isOriginalCosponsor: Option[Boolean],
  lastName: Option[String],
  party: Option[String],
  sponsorshipDate: Option[String],
  state: Option[String],
  url: Option[String],
)

object CoSponsorDTO {
  implicit val encoder: Encoder[CoSponsorDTO] = deriveEncoder[CoSponsorDTO]
  implicit val decoder: Decoder[CoSponsorDTO] = deriveDecoder[CoSponsorDTO]
}

final case class BillActionDTO(
  actionCode: Option[String],
  actionDate: String,
  sourceSystem: Option[SourceSystemDTO],
  text: String,
  actionType: Option[String],
)

object BillActionDTO {

  implicit val encoder: Encoder[BillActionDTO] = Encoder.instance { a =>
    val fields = List(
      a.actionCode.map(v => "actionCode" -> Json.fromString(v)),
      Some("actionDate" -> Json.fromString(a.actionDate)),
      a.sourceSystem.map(v => "sourceSystem" -> SourceSystemDTO.encoder.apply(v)),
      Some("text" -> Json.fromString(a.text)),
      a.actionType.map(v => "type" -> Json.fromString(v)),
    ).flatten
    Json.obj(fields*)
  }

  implicit val decoder: Decoder[BillActionDTO] = (c: HCursor) =>
    for {
      actionCode   <- c.downField("actionCode").as[Option[String]]
      actionDate   <- c.downField("actionDate").as[String]
      sourceSystem <- c.downField("sourceSystem").as[Option[SourceSystemDTO]]
      text         <- c.downField("text").as[String]
      actionType   <- c.downField("type").as[Option[String]]
    } yield BillActionDTO(
      actionCode = actionCode,
      actionDate = actionDate,
      sourceSystem = sourceSystem,
      text = text,
      actionType = actionType,
    )

}

final case class LegislativeSubjectDTO(
  name: String,
  updateDate: Option[String],
)

object LegislativeSubjectDTO {
  implicit val encoder: Encoder[LegislativeSubjectDTO] = deriveEncoder[LegislativeSubjectDTO]
  implicit val decoder: Decoder[LegislativeSubjectDTO] = deriveDecoder[LegislativeSubjectDTO]
}

final case class BillSubjectsDTO(
  legislativeSubjects: Option[List[LegislativeSubjectDTO]],
  policyArea: Option[String],
)

object BillSubjectsDTO {
  implicit val encoder: Encoder[BillSubjectsDTO] = deriveEncoder[BillSubjectsDTO]
  implicit val decoder: Decoder[BillSubjectsDTO] = deriveDecoder[BillSubjectsDTO]
}

final case class BillSummaryDTO(
  actionDate: Option[String],
  actionDesc: Option[String],
  text: Option[String],
  updateDate: Option[String],
  versionCode: Option[String],
)

object BillSummaryDTO {
  implicit val encoder: Encoder[BillSummaryDTO] = deriveEncoder[BillSummaryDTO]
  implicit val decoder: Decoder[BillSummaryDTO] = deriveDecoder[BillSummaryDTO]
}

final case class RelationshipDetailDTO(
  identifiedBy: Option[String],
  relationshipType: Option[String],
)

object RelationshipDetailDTO {

  implicit val encoder: Encoder[RelationshipDetailDTO] = Encoder.instance { r =>
    val fields = List(
      r.identifiedBy.map(v => "identifiedBy" -> Json.fromString(v)),
      r.relationshipType.map(v => "type" -> Json.fromString(v)),
    ).flatten
    Json.obj(fields*)
  }

  implicit val decoder: Decoder[RelationshipDetailDTO] = (c: HCursor) =>
    for {
      identifiedBy     <- c.downField("identifiedBy").as[Option[String]]
      relationshipType <- c.downField("type").as[Option[String]]
    } yield RelationshipDetailDTO(
      identifiedBy = identifiedBy,
      relationshipType = relationshipType,
    )

}

final case class RelatedBillDTO(
  congress: Option[Int],
  number: Option[Int],
  latestAction: Option[LatestActionDTO],
  relationshipDetails: Option[List[RelationshipDetailDTO]],
)

object RelatedBillDTO {
  implicit val encoder: Encoder[RelatedBillDTO] = deriveEncoder[RelatedBillDTO]
  implicit val decoder: Decoder[RelatedBillDTO] = deriveDecoder[RelatedBillDTO]
}

final case class CboCostEstimateDTO(
  description: Option[String],
  pubDate: Option[String],
  title: Option[String],
  url: Option[String],
)

object CboCostEstimateDTO {
  implicit val encoder: Encoder[CboCostEstimateDTO] = deriveEncoder[CboCostEstimateDTO]
  implicit val decoder: Decoder[CboCostEstimateDTO] = deriveDecoder[CboCostEstimateDTO]
}

final case class CommitteeReportDTO(
  citation: Option[String],
  url: Option[String],
)

object CommitteeReportDTO {
  implicit val encoder: Encoder[CommitteeReportDTO] = deriveEncoder[CommitteeReportDTO]
  implicit val decoder: Decoder[CommitteeReportDTO] = deriveDecoder[CommitteeReportDTO]
}

final case class TitleDTO(
  title: String,
  updateDate: Option[String],
  titleType: Option[String],
  titleTypeCode: Option[Int],
  billTextVersionCode: Option[String],
  billTextVersionName: Option[String],
)

object TitleDTO {
  implicit val encoder: Encoder[TitleDTO] = deriveEncoder[TitleDTO]
  implicit val decoder: Decoder[TitleDTO] = deriveDecoder[TitleDTO]
}

final case class BillListItemDTO(
  congress: Int,
  number: String,
  billType: String,
  latestAction: Option[LatestActionDTO],
  originChamber: Option[String],
  originChamberCode: Option[String],
  title: String,
  updateDate: Option[String],
  updateDateIncludingText: Option[String],
  url: String,
)

object BillListItemDTO {

  implicit val encoder: Encoder[BillListItemDTO] = Encoder.instance { b =>
    val fields = List(
      Some("congress" -> Json.fromInt(b.congress)),
      Some("number"   -> Json.fromString(b.number)),
      Some("type"     -> Json.fromString(b.billType)),
      b.latestAction.map(v => "latestAction" -> LatestActionDTO.encoder.apply(v)),
      b.originChamber.map(v => "originChamber" -> Json.fromString(v)),
      b.originChamberCode.map(v => "originChamberCode" -> Json.fromString(v)),
      Some("title" -> Json.fromString(b.title)),
      b.updateDate.map(v => "updateDate" -> Json.fromString(v)),
      b.updateDateIncludingText.map(v => "updateDateIncludingText" -> Json.fromString(v)),
      Some("url" -> Json.fromString(b.url)),
    ).flatten
    Json.obj(fields*)
  }

  implicit val decoder: Decoder[BillListItemDTO] = (c: HCursor) =>
    for {
      congress                <- c.downField("congress").as[Int]
      number                  <- c.downField("number").as[String]
      billType                <- c.downField("type").as[String].orElse(c.downField("billType").as[String])
      latestAction            <- c.downField("latestAction").as[Option[LatestActionDTO]]
      originChamber           <- c.downField("originChamber").as[Option[String]]
      originChamberCode       <- c.downField("originChamberCode").as[Option[String]]
      title                   <- c.downField("title").as[String]
      updateDate              <- c.downField("updateDate").as[Option[String]]
      updateDateIncludingText <- c.downField("updateDateIncludingText").as[Option[String]]
      url                     <- c.downField("url").as[String]
    } yield BillListItemDTO(
      congress = congress,
      number = number,
      billType = billType,
      latestAction = latestAction,
      originChamber = originChamber,
      originChamberCode = originChamberCode,
      title = title,
      updateDate = updateDate,
      updateDateIncludingText = updateDateIncludingText,
      url = url,
    )

}

final case class BillDetailDTO(
  congress: Int,
  number: String,
  billType: String,
  latestAction: Option[LatestActionDTO],
  originChamber: Option[String],
  originChamberCode: Option[String],
  title: String,
  updateDate: Option[String],
  updateDateIncludingText: Option[String],
  url: String,
  introducedDate: Option[String],
  policyArea: Option[String],
  sponsors: Option[List[SponsorDTO]],
  cosponsors: Option[PaginationInfoDTO],
  subjects: Option[BillSubjectsDTO],
  summaries: Option[List[BillSummaryDTO]],
  actions: Option[List[BillActionDTO]],
  committees: Option[List[String]],
  textVersions: Option[List[TextVersionDTO]],
  titles: Option[List[TitleDTO]],
  constitutionalAuthorityStatementText: Option[String],
  cboCostEstimates: Option[List[CboCostEstimateDTO]],
  committeeReports: Option[List[CommitteeReportDTO]],
  relatedBills: Option[List[RelatedBillDTO]],
  legislationUrl: Option[String],
)

object BillDetailDTO {
  implicit val encoder: Encoder[BillDetailDTO] = deriveEncoder[BillDetailDTO]

  implicit val decoder: Decoder[BillDetailDTO] = (c: HCursor) =>
    for {
      congress                             <- c.downField("congress").as[Int]
      number                               <- c.downField("number").as[String]
      billType                             <- c.downField("type").as[String].orElse(c.downField("billType").as[String])
      latestAction                         <- c.downField("latestAction").as[Option[LatestActionDTO]]
      originChamber                        <- c.downField("originChamber").as[Option[String]]
      originChamberCode                    <- c.downField("originChamberCode").as[Option[String]]
      title                                <- c.downField("title").as[String]
      updateDate                           <- c.downField("updateDate").as[Option[String]]
      updateDateIncludingText              <- c.downField("updateDateIncludingText").as[Option[String]]
      url                                  <- c.downField("url").as[String]
      introducedDate                       <- c.downField("introducedDate").as[Option[String]]
      policyArea                           <- c.downField("policyArea").as[Option[String]]
      sponsors                             <- c.downField("sponsors").as[Option[List[SponsorDTO]]]
      cosponsors                           <- c.downField("cosponsors").as[Option[PaginationInfoDTO]]
      subjects                             <- c.downField("subjects").as[Option[BillSubjectsDTO]]
      summaries                            <- c.downField("summaries").as[Option[List[BillSummaryDTO]]]
      actions                              <- c.downField("actions").as[Option[List[BillActionDTO]]]
      committees                           <- c.downField("committees").as[Option[List[String]]]
      textVersions                         <- c.downField("textVersions").as[Option[List[TextVersionDTO]]]
      titles                               <- c.downField("titles").as[Option[List[TitleDTO]]]
      constitutionalAuthorityStatementText <- c.downField("constitutionalAuthorityStatementText").as[Option[String]]
      cboCostEstimates                     <- c.downField("cboCostEstimates").as[Option[List[CboCostEstimateDTO]]]
      committeeReports                     <- c.downField("committeeReports").as[Option[List[CommitteeReportDTO]]]
      relatedBills                         <- c.downField("relatedBills").as[Option[List[RelatedBillDTO]]]
      legislationUrl                       <- c.downField("legislationUrl").as[Option[String]]
    } yield BillDetailDTO(
      congress = congress,
      number = number,
      billType = billType,
      latestAction = latestAction,
      originChamber = originChamber,
      originChamberCode = originChamberCode,
      title = title,
      updateDate = updateDate,
      updateDateIncludingText = updateDateIncludingText,
      url = url,
      introducedDate = introducedDate,
      policyArea = policyArea,
      sponsors = sponsors,
      cosponsors = cosponsors,
      subjects = subjects,
      summaries = summaries,
      actions = actions,
      committees = committees,
      textVersions = textVersions,
      titles = titles,
      constitutionalAuthorityStatementText = constitutionalAuthorityStatementText,
      cboCostEstimates = cboCostEstimates,
      committeeReports = committeeReports,
      relatedBills = relatedBills,
      legislationUrl = legislationUrl,
    )

}

final case class BillListResponseDTO(
  items: List[BillListItemDTO],
  pagination: Option[PaginationInfoDTO],
) extends PagedObject[BillListItemDTO]

object BillListResponseDTO {
  import cats.Semigroup

  implicit val semigroup: Semigroup[BillListResponseDTO] = Semigroup.instance { (a, b) =>
    BillListResponseDTO(
      items = a.items ++ b.items,
      pagination = b.pagination,
    )
  }

  implicit val encoder: Encoder[BillListResponseDTO] = deriveEncoder[BillListResponseDTO]

  implicit val decoder: Decoder[BillListResponseDTO] = Decoder.instance { c =>
    for {
      items <- c.downField("bills").as[List[BillListItemDTO]].orElse(c.downField("items").as[List[BillListItemDTO]])
      pagination <- c.downField("pagination").as[Option[PaginationInfoDTO]]
    } yield BillListResponseDTO(items = items, pagination = pagination)
  }

}

final case class CosponsorListResponseDTO(
  cosponsors: List[CoSponsorDTO],
  pagination: Option[PaginationInfoDTO],
) extends PagedObject[CoSponsorDTO] {
  override def items: List[CoSponsorDTO] = cosponsors
}

object CosponsorListResponseDTO {
  import cats.Semigroup

  implicit val semigroup: Semigroup[CosponsorListResponseDTO] = Semigroup.instance { (a, b) =>
    CosponsorListResponseDTO(
      cosponsors = a.cosponsors ++ b.cosponsors,
      pagination = b.pagination,
    )
  }

  implicit val decoder: Decoder[CosponsorListResponseDTO] = Decoder.instance { c =>
    for {
      cosponsors <- c.downField("cosponsors").as[List[CoSponsorDTO]]
      pagination <- c.downField("pagination").as[Option[PaginationInfoDTO]]
    } yield CosponsorListResponseDTO(cosponsors, pagination)
  }

  implicit val encoder: Encoder[CosponsorListResponseDTO] = deriveEncoder[CosponsorListResponseDTO]
}
