package repcheck.shared.models.analysis

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillFiscalEstimateDO(
  fiscalEstimateId: UUID,
  analysisId: UUID,
  billId: String,
  conceptGroupId: Option[UUID],
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
