package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.llm.StanceType

final case class TopicStance(
  topic: String,
  stance: StanceType,
  confidence: Double,
  reasoning: String,
)

object TopicStance {

  implicit val encoder: Encoder[TopicStance] = deriveEncoder[TopicStance]
  implicit val decoder: Decoder[TopicStance] = deriveDecoder[TopicStance]

}

final case class StanceClassificationOutput(
  stances: List[TopicStance]
)

object StanceClassificationOutput {

  implicit val encoder: Encoder[StanceClassificationOutput] = deriveEncoder[StanceClassificationOutput]
  implicit val decoder: Decoder[StanceClassificationOutput] = deriveDecoder[StanceClassificationOutput]

}
