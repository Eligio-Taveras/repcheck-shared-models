package repcheck.shared.models.congress.dos.member

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class MemberPartyHistoryDO(
  id: Long,
  memberId: Long,
  partyName: Option[String],
  partyAbbreviation: Option[String],
  startYear: Option[Int],
)

object MemberPartyHistoryDO {

  implicit val encoder: Encoder[MemberPartyHistoryDO] = deriveEncoder[MemberPartyHistoryDO]
  implicit val decoder: Decoder[MemberPartyHistoryDO] = deriveDecoder[MemberPartyHistoryDO]

}
