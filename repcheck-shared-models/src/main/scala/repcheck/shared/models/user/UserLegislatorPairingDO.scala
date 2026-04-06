package repcheck.shared.models.user

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class UserLegislatorPairingDO(
  userId: UUID,
  memberId: Long,
  state: String,
  district: Option[Int],
  chamber: String,
  pairedAt: Option[Instant],
  validatedAt: Option[Instant],
)

object UserLegislatorPairingDO {

  implicit val encoder: Encoder[UserLegislatorPairingDO] = deriveEncoder[UserLegislatorPairingDO]
  implicit val decoder: Decoder[UserLegislatorPairingDO] = deriveDecoder[UserLegislatorPairingDO]

}
