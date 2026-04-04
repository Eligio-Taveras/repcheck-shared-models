package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillSummaryOutput(
  summary: String,
  readingLevel: String,
  keyPoints: List[String],
)

object BillSummaryOutput {

  implicit val encoder: Encoder[BillSummaryOutput] = deriveEncoder[BillSummaryOutput]
  implicit val decoder: Decoder[BillSummaryOutput] = deriveDecoder[BillSummaryOutput]

}
