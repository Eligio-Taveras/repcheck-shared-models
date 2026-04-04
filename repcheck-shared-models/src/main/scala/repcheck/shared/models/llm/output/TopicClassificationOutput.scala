package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class TopicScore(
  topic: String,
  confidence: Double,
)

object TopicScore {

  implicit val encoder: Encoder[TopicScore] = deriveEncoder[TopicScore]
  implicit val decoder: Decoder[TopicScore] = deriveDecoder[TopicScore]

}

final case class TopicClassificationOutput(
  topics: List[TopicScore]
)

object TopicClassificationOutput {

  implicit val encoder: Encoder[TopicClassificationOutput] = deriveEncoder[TopicClassificationOutput]
  implicit val decoder: Decoder[TopicClassificationOutput] = deriveDecoder[TopicClassificationOutput]

}
