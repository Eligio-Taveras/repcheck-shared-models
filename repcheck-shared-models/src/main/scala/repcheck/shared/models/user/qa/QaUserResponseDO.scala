package repcheck.shared.models.user.qa

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class QaUserResponseDO(
  responseId: UUID,
  userId: UUID,
  questionId: String,
  selectedOption: Option[String],
  customText: Option[String],
  respondedAt: Option[Instant],
)

object QaUserResponseDO {

  implicit val encoder: Encoder[QaUserResponseDO] = deriveEncoder[QaUserResponseDO]
  implicit val decoder: Decoder[QaUserResponseDO] = deriveDecoder[QaUserResponseDO]

}
