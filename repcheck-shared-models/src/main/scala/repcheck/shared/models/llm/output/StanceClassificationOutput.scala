package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import org.scalacheck.Gen
import repcheck.shared.models.llm.StanceType
import repcheck.shared.models.llm.codec.StructuredCodec

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

  private val example =
    StanceClassificationOutput(
      List(TopicStance("immigration enforcement", StanceType.Progressive, 0.92, "Expands protections."))
    )

  private val gen: Gen[StanceClassificationOutput] = {
    val topicStanceGen = for {
      topic      <- Gen.alphaNumStr
      stance     <- Gen.oneOf(StanceType.values.toIndexedSeq)
      confidence <- Gen.choose(0.0, 1.0)
      reasoning  <- Gen.alphaNumStr
    } yield TopicStance(topic, stance, confidence, reasoning)
    for {
      n     <- Gen.choose(0, 4)
      items <- Gen.listOfN(n, topicStanceGen)
    } yield StanceClassificationOutput(items)
  }

  given StructuredCodec[StanceClassificationOutput] =
    StructuredCodec.instance(AnalysisTapirSchemas.stanceClassificationOutput, example, gen)

}
