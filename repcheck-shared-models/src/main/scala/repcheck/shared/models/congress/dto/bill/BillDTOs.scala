package repcheck.shared.models.congress.dto.bill

import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import repcheck.shared.models.congress.dto.common.PaginationInfoDTO

final case class LatestActionDTO(
    actionDate: String,
    text: String
)

object LatestActionDTO {
  implicit val encoder: Encoder[LatestActionDTO] = deriveEncoder[LatestActionDTO]
  implicit val decoder: Decoder[LatestActionDTO] = deriveDecoder[LatestActionDTO]
}

final case class SourceSystemDTO(
    code: Option[Int],
    name: Option[String]
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
    url: Option[String]
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
    url: Option[String]
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
    actionType: Option[String]
)

object BillActionDTO {
  implicit val encoder: Encoder[BillActionDTO] = Encoder.instance { a =>
    val fields = List(
      a.actionCode.map(v => "actionCode" -> Json.fromString(v)),
      Some("actionDate" -> Json.fromString(a.actionDate)),
      a.sourceSystem.map(v => "sourceSystem" -> SourceSystemDTO.encoder.apply(v)),
      Some("text" -> Json.fromString(a.text)),
      a.actionType.map(v => "type" -> Json.fromString(v))
    ).flatten
    Json.obj(fields*)
  }

  implicit val decoder: Decoder[BillActionDTO] = (c: HCursor) => {
    for {
      actionCode <- c.downField("actionCode").as[Option[String]]
      actionDate <- c.downField("actionDate").as[String]
      sourceSystem <- c.downField("sourceSystem").as[Option[SourceSystemDTO]]
      text <- c.downField("text").as[String]
      actionType <- c.downField("type").as[Option[String]]
    } yield BillActionDTO(
      actionCode = actionCode,
      actionDate = actionDate,
      sourceSystem = sourceSystem,
      text = text,
      actionType = actionType
    )
  }
}

final case class LegislativeSubjectDTO(
    name: String,
    updateDate: Option[String]
)

object LegislativeSubjectDTO {
  implicit val encoder: Encoder[LegislativeSubjectDTO] = deriveEncoder[LegislativeSubjectDTO]
  implicit val decoder: Decoder[LegislativeSubjectDTO] = deriveDecoder[LegislativeSubjectDTO]
}

final case class BillSubjectsDTO(
    legislativeSubjects: Option[List[LegislativeSubjectDTO]],
    policyArea: Option[String]
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
    versionCode: Option[String]
)

object BillSummaryDTO {
  implicit val encoder: Encoder[BillSummaryDTO] = deriveEncoder[BillSummaryDTO]
  implicit val decoder: Decoder[BillSummaryDTO] = deriveDecoder[BillSummaryDTO]
}

final case class RelationshipDetailDTO(
    identifiedBy: Option[String],
    relationshipType: Option[String]
)

object RelationshipDetailDTO {
  implicit val encoder: Encoder[RelationshipDetailDTO] = Encoder.instance { r =>
    val fields = List(
      r.identifiedBy.map(v => "identifiedBy" -> Json.fromString(v)),
      r.relationshipType.map(v => "type" -> Json.fromString(v))
    ).flatten
    Json.obj(fields*)
  }

  implicit val decoder: Decoder[RelationshipDetailDTO] = (c: HCursor) => {
    for {
      identifiedBy <- c.downField("identifiedBy").as[Option[String]]
      relationshipType <- c.downField("type").as[Option[String]]
    } yield RelationshipDetailDTO(
      identifiedBy = identifiedBy,
      relationshipType = relationshipType
    )
  }
}

final case class RelatedBillDTO(
    congress: Option[Int],
    number: Option[Int],
    latestAction: Option[LatestActionDTO],
    relationshipDetails: Option[List[RelationshipDetailDTO]]
)

object RelatedBillDTO {
  implicit val encoder: Encoder[RelatedBillDTO] = deriveEncoder[RelatedBillDTO]
  implicit val decoder: Decoder[RelatedBillDTO] = deriveDecoder[RelatedBillDTO]
}

final case class CboCostEstimateDTO(
    description: Option[String],
    pubDate: Option[String],
    title: Option[String],
    url: Option[String]
)

object CboCostEstimateDTO {
  implicit val encoder: Encoder[CboCostEstimateDTO] = deriveEncoder[CboCostEstimateDTO]
  implicit val decoder: Decoder[CboCostEstimateDTO] = deriveDecoder[CboCostEstimateDTO]
}

final case class CommitteeReportDTO(
    citation: Option[String],
    url: Option[String]
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
    billTextVersionName: Option[String]
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
    url: String
)

object BillListItemDTO {
  implicit val encoder: Encoder[BillListItemDTO] = deriveEncoder[BillListItemDTO]
  implicit val decoder: Decoder[BillListItemDTO] = deriveDecoder[BillListItemDTO]
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
    textVersions: Option[List[TextVersionDTO]],
    titles: Option[List[TitleDTO]],
    constitutionalAuthorityStatementText: Option[String],
    cboCostEstimates: Option[List[CboCostEstimateDTO]],
    committeeReports: Option[List[CommitteeReportDTO]],
    relatedBills: Option[List[RelatedBillDTO]],
    legislationUrl: Option[String]
)

object BillDetailDTO {
  implicit val encoder: Encoder[BillDetailDTO] = deriveEncoder[BillDetailDTO]
  implicit val decoder: Decoder[BillDetailDTO] = deriveDecoder[BillDetailDTO]
}
