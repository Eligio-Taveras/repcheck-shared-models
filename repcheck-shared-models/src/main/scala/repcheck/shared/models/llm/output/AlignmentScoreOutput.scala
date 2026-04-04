package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class TopicAlignmentScore(
  topic: String,
  score: Double,
  explanation: String,
)

object TopicAlignmentScore {

  implicit val encoder: Encoder[TopicAlignmentScore] = deriveEncoder[TopicAlignmentScore]
  implicit val decoder: Decoder[TopicAlignmentScore] = deriveDecoder[TopicAlignmentScore]

}

final case class AlignmentHighlight(
  billId: String,
  topic: String,
  stance: String,
  vote: String,
  alignment: Double,
)

object AlignmentHighlight {

  implicit val encoder: Encoder[AlignmentHighlight] = deriveEncoder[AlignmentHighlight]
  implicit val decoder: Decoder[AlignmentHighlight] = deriveDecoder[AlignmentHighlight]

}

final case class AlignmentScoreOutput(
  topicScores: List[TopicAlignmentScore],
  overallScore: Double,
  highlights: List[AlignmentHighlight],
  reasoning: String,
)

object AlignmentScoreOutput {

  implicit val encoder: Encoder[AlignmentScoreOutput] = deriveEncoder[AlignmentScoreOutput]
  implicit val decoder: Decoder[AlignmentScoreOutput] = deriveDecoder[AlignmentScoreOutput]

}
