package repcheck.shared.models.user.qa

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class QaAnswerOptionDO(
  id: Long,
  questionId: Long,
  optionValue: String,
  displayText: String,
  stanceMultiplier: Float,
  importanceSignal: Int,
  displayOrder: Int,
)

object QaAnswerOptionDO {

  implicit val encoder: Encoder[QaAnswerOptionDO] = deriveEncoder[QaAnswerOptionDO]
  implicit val decoder: Decoder[QaAnswerOptionDO] = deriveDecoder[QaAnswerOptionDO]

}
