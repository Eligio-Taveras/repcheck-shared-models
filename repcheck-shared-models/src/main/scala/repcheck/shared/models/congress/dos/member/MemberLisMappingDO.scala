package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class MemberLisMappingDO(
  id: Long,
  memberId: Long,
  lisMemberId: Long,
  lastVerified: Instant,
)

object MemberLisMappingDO {

  implicit val encoder: Encoder[MemberLisMappingDO] = deriveEncoder[MemberLisMappingDO]
  implicit val decoder: Decoder[MemberLisMappingDO] = deriveDecoder[MemberLisMappingDO]

}
