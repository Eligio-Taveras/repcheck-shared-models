package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.common.{Party, UsState}

final case class MemberHistoryDO(
  id: Long,
  memberId: Long,
  firstName: Option[String],
  lastName: Option[String],
  directOrderName: Option[String],
  invertedOrderName: Option[String],
  honorificName: Option[String],
  birthYear: Option[Int],
  currentParty: Option[Party],
  state: Option[UsState],
  district: Option[Int],
  imageUrl: Option[String],
  imageAttribution: Option[String],
  officialUrl: Option[String],
  updateDate: Option[String],
  archivedAt: Option[Instant],
)

object MemberHistoryDO {

  implicit val encoder: Encoder[MemberHistoryDO] = deriveEncoder[MemberHistoryDO]
  implicit val decoder: Decoder[MemberHistoryDO] = deriveDecoder[MemberHistoryDO]

}
