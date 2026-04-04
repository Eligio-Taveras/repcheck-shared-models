package repcheck.shared.models.user

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * User preference domain object.
 *
 * `importance` is constrained to 1-10. This is enforced at the database level via a CHECK constraint. Callers should
 * validate before persisting.
 */
final case class UserPreferenceDO(
  preferenceId: UUID,
  userId: UUID,
  topic: String,
  stance: String,
  importance: Int,
  embedding: Option[Array[Float]],
  updatedAt: Option[Instant],
)

object UserPreferenceDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[UserPreferenceDO] = deriveEncoder[UserPreferenceDO]
  implicit val decoder: Decoder[UserPreferenceDO] = deriveDecoder[UserPreferenceDO]

}
