package repcheck.shared.models.analysis

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillAnalysisTopicDO(
  id: Long,
  analysisId: Long,
  billId: Long,
  conceptGroupId: Option[String],
  passNumber: Int,
  topic: String,
  confidence: Float,
)

object BillAnalysisTopicDO {

  implicit val encoder: Encoder[BillAnalysisTopicDO] = deriveEncoder[BillAnalysisTopicDO]
  implicit val decoder: Decoder[BillAnalysisTopicDO] = deriveDecoder[BillAnalysisTopicDO]

}
