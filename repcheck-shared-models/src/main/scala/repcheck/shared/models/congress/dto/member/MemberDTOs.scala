package repcheck.shared.models.congress.dto.member

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, HCursor, Json}

import repcheck.shared.models.congress.dto.common.PaginationInfoDTO

final case class MemberDepictionDTO(
  imageUrl: Option[String],
  attribution: Option[String],
)

object MemberDepictionDTO {
  implicit val encoder: Encoder[MemberDepictionDTO] = deriveEncoder[MemberDepictionDTO]
  implicit val decoder: Decoder[MemberDepictionDTO] = deriveDecoder[MemberDepictionDTO]
}

final case class MemberTermSummaryDTO(
  chamber: Option[String],
  startYear: Option[Int],
)

object MemberTermSummaryDTO {
  implicit val encoder: Encoder[MemberTermSummaryDTO] = deriveEncoder[MemberTermSummaryDTO]
  implicit val decoder: Decoder[MemberTermSummaryDTO] = deriveDecoder[MemberTermSummaryDTO]
}

final case class MemberDetailTermDTO(
  chamber: Option[String],
  congress: Option[Int],
  endYear: Option[Int],
  memberType: Option[String],
  startYear: Option[Int],
  stateCode: Option[String],
  stateName: Option[String],
  district: Option[Int],
)

object MemberDetailTermDTO {
  implicit val encoder: Encoder[MemberDetailTermDTO] = deriveEncoder[MemberDetailTermDTO]
  implicit val decoder: Decoder[MemberDetailTermDTO] = deriveDecoder[MemberDetailTermDTO]
}

final case class PartyHistoryDTO(
  partyAbbreviation: Option[String],
  partyName: Option[String],
  startYear: Option[Int],
)

object PartyHistoryDTO {
  implicit val encoder: Encoder[PartyHistoryDTO] = deriveEncoder[PartyHistoryDTO]
  implicit val decoder: Decoder[PartyHistoryDTO] = deriveDecoder[PartyHistoryDTO]
}

final case class LeadershipDTO(
  congress: Option[Int],
  type_ : Option[String],
)

object LeadershipDTO {

  implicit val encoder: Encoder[LeadershipDTO] = Encoder.instance { l =>
    val fields = List(
      l.congress.map(v => "congress" -> Json.fromInt(v)),
      l.type_.map(v => "type" -> Json.fromString(v)),
    ).flatten
    Json.obj(fields*)
  }

  implicit val decoder: Decoder[LeadershipDTO] = (c: HCursor) =>
    for {
      congress <- c.downField("congress").as[Option[Int]]
      t        <- c.downField("type").as[Option[String]]
    } yield LeadershipDTO(congress = congress, type_ = t)

}

final case class MemberListItemDTO(
  bioguideId: String,
  name: Option[String],
  partyName: Option[String],
  state: Option[String],
  depiction: Option[MemberDepictionDTO],
  terms: Option[List[MemberTermSummaryDTO]],
  updateDate: Option[String],
  url: Option[String],
)

object MemberListItemDTO {
  implicit val encoder: Encoder[MemberListItemDTO] = deriveEncoder[MemberListItemDTO]
  implicit val decoder: Decoder[MemberListItemDTO] = deriveDecoder[MemberListItemDTO]
}

final case class MemberDetailDTO(
  bioguideId: String,
  birthYear: Option[String],
  firstName: Option[String],
  lastName: Option[String],
  directOrderName: Option[String],
  invertedOrderName: Option[String],
  honorificName: Option[String],
  cosponsoredLegislation: Option[PaginationInfoDTO],
  depiction: Option[MemberDepictionDTO],
  leadership: Option[List[LeadershipDTO]],
  partyHistory: Option[List[PartyHistoryDTO]],
  sponsoredLegislation: Option[PaginationInfoDTO],
  state: Option[String],
  terms: Option[List[MemberDetailTermDTO]],
  updateDate: Option[String],
)

object MemberDetailDTO {
  implicit val encoder: Encoder[MemberDetailDTO] = deriveEncoder[MemberDetailDTO]
  implicit val decoder: Decoder[MemberDetailDTO] = deriveDecoder[MemberDetailDTO]
}
