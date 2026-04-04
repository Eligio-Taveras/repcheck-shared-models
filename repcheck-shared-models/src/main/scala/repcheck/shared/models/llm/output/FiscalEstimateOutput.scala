package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class FiscalEstimateOutput(
  estimatedCost: String,
  timeframe: String,
  assumptions: List[String],
  confidence: Double,
)

object FiscalEstimateOutput {

  implicit val encoder: Encoder[FiscalEstimateOutput] = deriveEncoder[FiscalEstimateOutput]
  implicit val decoder: Decoder[FiscalEstimateOutput] = deriveDecoder[FiscalEstimateOutput]

}
