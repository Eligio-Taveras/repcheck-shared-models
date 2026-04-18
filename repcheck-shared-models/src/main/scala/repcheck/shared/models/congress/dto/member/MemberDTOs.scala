package repcheck.shared.models.congress.dto.member

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor, Json}

import repcheck.shared.models.congress.dto.common.{PagedObject, PaginationInfoDTO}

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

  // Congress.gov encodes `terms` as `{"item": [...]}` in the member list endpoint.
  // Auto-derived encoder/decoder cannot handle this wrapping, so we use custom instances.
  implicit val encoder: Encoder[MemberListItemDTO] = Encoder.instance { m =>
    Json.obj(
      "bioguideId" -> Json.fromString(m.bioguideId),
      "name"       -> m.name.asJson,
      "partyName"  -> m.partyName.asJson,
      "state"      -> m.state.asJson,
      "depiction"  -> m.depiction.asJson,
      "terms"      -> m.terms.fold(Json.Null)(items => Json.obj("item" -> items.asJson)),
      "updateDate" -> m.updateDate.asJson,
      "url"        -> m.url.asJson,
    )
  }

  implicit val decoder: Decoder[MemberListItemDTO] = Decoder.instance { c =>
    for {
      bioguideId <- c.downField("bioguideId").as[String]
      name       <- c.downField("name").as[Option[String]]
      partyName  <- c.downField("partyName").as[Option[String]]
      state      <- c.downField("state").as[Option[String]]
      depiction  <- c.downField("depiction").as[Option[MemberDepictionDTO]]
      termsJson  <- c.downField("terms").as[Option[Json]]
      terms <- termsJson match {
        case None    => Right(None)
        case Some(j) => j.hcursor.downField("item").as[Option[List[MemberTermSummaryDTO]]]
      }
      updateDate <- c.downField("updateDate").as[Option[String]]
      url        <- c.downField("url").as[Option[String]]
    } yield MemberListItemDTO(bioguideId, name, partyName, state, depiction, terms, updateDate, url)
  }

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

final case class MemberListResponseDTO(
  items: List[MemberListItemDTO],
  pagination: Option[PaginationInfoDTO],
) extends PagedObject[MemberListItemDTO]

object MemberListResponseDTO {
  import cats.Semigroup

  implicit val semigroup: Semigroup[MemberListResponseDTO] = Semigroup.instance { (a, b) =>
    MemberListResponseDTO(
      items = a.items ++ b.items,
      pagination = b.pagination,
    )
  }

  implicit val encoder: Encoder[MemberListResponseDTO] = deriveEncoder[MemberListResponseDTO]
  implicit val decoder: Decoder[MemberListResponseDTO] = deriveDecoder[MemberListResponseDTO]
}
