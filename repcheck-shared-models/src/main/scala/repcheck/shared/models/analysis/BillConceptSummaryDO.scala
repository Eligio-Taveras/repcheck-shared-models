package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillConceptSummaryDO(
  id: Long,
  analysisId: Long,
  billId: Long,
  conceptGroupId: Option[Long],
  passNumber: Int,
  topics: List[String],
  summary: Option[String],
  readingLevel: Option[String],
  keyPoints: List[String],
  embedding: Option[Array[Float]],
  createdAt: Option[Instant],
)

object BillConceptSummaryDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillConceptSummaryDO] = deriveEncoder[BillConceptSummaryDO]
  implicit val decoder: Decoder[BillConceptSummaryDO] = deriveDecoder[BillConceptSummaryDO]

}
