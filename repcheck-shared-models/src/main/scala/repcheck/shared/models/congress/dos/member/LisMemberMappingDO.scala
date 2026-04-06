package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class LisMemberMappingDO(
  lisMemberId: String,
  memberId: Long,
  lastVerified: Instant,
)

object LisMemberMappingDO {

  implicit val encoder: Encoder[LisMemberMappingDO] = deriveEncoder[LisMemberMappingDO]
  implicit val decoder: Decoder[LisMemberMappingDO] = deriveDecoder[LisMemberMappingDO]

}
