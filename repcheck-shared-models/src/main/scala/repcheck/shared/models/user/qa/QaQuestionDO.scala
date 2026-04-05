package repcheck.shared.models.user.qa

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class QaQuestionDO(
  questionId: String,
  questionText: String,
  category: String,
  displayOrder: Int,
  allowCustom: Boolean,
  active: Boolean,
  createdAt: Option[Instant],
)

object QaQuestionDO {

  implicit val encoder: Encoder[QaQuestionDO] = deriveEncoder[QaQuestionDO]
  implicit val decoder: Decoder[QaQuestionDO] = deriveDecoder[QaQuestionDO]

}
