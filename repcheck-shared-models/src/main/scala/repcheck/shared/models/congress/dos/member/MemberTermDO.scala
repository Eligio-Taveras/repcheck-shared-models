package repcheck.shared.models.congress.dos.member

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class MemberTermDO(
  termId: Long,
  memberId: Long,
  chamber: Option[String],
  congress: Option[Int],
  startYear: Option[Int],
  endYear: Option[Int],
  memberType: Option[String],
  stateCode: Option[String],
  stateName: Option[String],
  district: Option[Int],
)

object MemberTermDO {

  implicit val encoder: Encoder[MemberTermDO] = deriveEncoder[MemberTermDO]
  implicit val decoder: Decoder[MemberTermDO] = deriveDecoder[MemberTermDO]

}
