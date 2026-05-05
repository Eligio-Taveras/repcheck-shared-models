package repcheck.shared.models.congress.dto.common

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, HCursor, Json}

final case class PaginationInfoDTO(
  count: Option[Int],
  url: Option[String],
)

object PaginationInfoDTO {
  implicit val encoder: Encoder[PaginationInfoDTO] = deriveEncoder[PaginationInfoDTO]

  implicit val decoder: Decoder[PaginationInfoDTO] = Decoder.instance { c =>
    for {
      count <- c.downField("count").as[Option[Int]]
      url <- c.downField("url").as[Option[String]].flatMap {
        case some @ Some(_) => Right(some)
        case None           => c.downField("next").as[Option[String]]
      }
    } yield PaginationInfoDTO(count = count, url = url)
  }

}

/**
 * Generic `{count, url}` link sub-object that Congress.gov detail responses use to point at sibling endpoints (e.g.
 * `actions`, `textVersions`, `cosponsors`, `amendmentsToAmendment` on `/amendment/{c}/{t}/{n}`). The detail endpoint
 * does NOT inline these collections — it returns a count + the URL to fetch them from.
 *
 * Distinct from [[PaginationInfoDTO]] semantically (which represents next-page chaining); same shape but reusing
 * pagination types for non-pagination link references obscures intent. New code should prefer this type.
 */
final case class ResourceLinkDTO(
  count: Option[Int],
  url: Option[String],
)

object ResourceLinkDTO {
  implicit val encoder: Encoder[ResourceLinkDTO] = deriveEncoder[ResourceLinkDTO]
  implicit val decoder: Decoder[ResourceLinkDTO] = deriveDecoder[ResourceLinkDTO]
}

final case class ApiListResponseDTO[T](
  items: List[T],
  pagination: Option[PaginationInfoDTO],
)

object ApiListResponseDTO {

  implicit def encoder[T](implicit enc: Encoder[T]): Encoder[ApiListResponseDTO[T]] =
    Encoder.instance { resp =>
      val fields = List(
        Some("items" -> Encoder.encodeList[T].apply(resp.items)),
        resp.pagination.map(p => "pagination" -> PaginationInfoDTO.encoder.apply(p)),
      ).flatten
      Json.obj(fields*)
    }

  implicit def decoder[T](implicit dec: Decoder[T]): Decoder[ApiListResponseDTO[T]] =
    (c: HCursor) =>
      for {
        items      <- c.downField("items").as[List[T]]
        pagination <- c.downField("pagination").as[Option[PaginationInfoDTO]]
      } yield ApiListResponseDTO(items = items, pagination = pagination)

}
