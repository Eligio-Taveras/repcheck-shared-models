package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class LisMemberDO(
  id: Long,
  naturalKey: String,
  createdAt: Option[Instant],
)

object LisMemberDO {

  implicit val encoder: Encoder[LisMemberDO] = deriveEncoder[LisMemberDO]
  implicit val decoder: Decoder[LisMemberDO] = deriveDecoder[LisMemberDO]

}
