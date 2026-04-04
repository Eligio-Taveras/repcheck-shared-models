package repcheck.shared.models.user

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class UserDO(
  userId: UUID,
  displayName: Option[String],
  email: Option[String],
  state: Option[String],
  district: Option[Int],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object UserDO {

  implicit val encoder: Encoder[UserDO] = deriveEncoder[UserDO]
  implicit val decoder: Decoder[UserDO] = deriveDecoder[UserDO]

}
