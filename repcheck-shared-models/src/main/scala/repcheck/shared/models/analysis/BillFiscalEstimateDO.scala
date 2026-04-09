package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillFiscalEstimateDO(
  id: Long,
  analysisId: Long,
  billId: Long,
  conceptGroupId: Option[Long],
  passNumber: Int,
  estimatedCost: String,
  timeframe: String,
  confidence: Float,
  assumptions: List[String],
  llmModel: String,
  createdAt: Option[Instant],
)

object BillFiscalEstimateDO {

  implicit val encoder: Encoder[BillFiscalEstimateDO] = deriveEncoder[BillFiscalEstimateDO]
  implicit val decoder: Decoder[BillFiscalEstimateDO] = deriveDecoder[BillFiscalEstimateDO]

}
