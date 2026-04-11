package repcheck.shared.models.congress.dos.member

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.common.{Chamber, UsState}
import repcheck.shared.models.congress.member.MemberType

final case class MemberTermDO(
  termId: Long,
  memberId: Long,
  chamber: Option[Chamber],
  congress: Option[Int],
  startYear: Option[Int],
  endYear: Option[Int],
  memberType: Option[MemberType],
  stateCode: Option[UsState],
  stateName: Option[String],
  district: Option[Int],
)

object MemberTermDO {

  implicit val encoder: Encoder[MemberTermDO] = deriveEncoder[MemberTermDO]
  implicit val decoder: Decoder[MemberTermDO] = deriveDecoder[MemberTermDO]

}
