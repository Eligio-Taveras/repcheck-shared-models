package repcheck.shared.models.user.qa

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class QaQuestionTopicDO(
  id: Long,
  questionId: Long,
  topic: String,
  agreeStance: String,
  weight: Float,
)

object QaQuestionTopicDO {

  implicit val encoder: Encoder[QaQuestionTopicDO] = deriveEncoder[QaQuestionTopicDO]
  implicit val decoder: Decoder[QaQuestionTopicDO] = deriveDecoder[QaQuestionTopicDO]

}
