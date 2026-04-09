package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillFindingDO(
  id: Long,
  billId: Long,
  analysisId: Option[Long],
  findingTypeId: Int,
  passNumber: Int,
  summary: String,
  details: Option[String],
  conceptGroupId: Option[String],
  severity: Option[String],
  confidence: Option[Float],
  affectedSection: Option[String],
  affectedGroup: Option[String],
  embedding: Option[Array[Float]],
  llmModel: String,
  analyzedAt: Option[Instant],
)

object BillFindingDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillFindingDO] = deriveEncoder[BillFindingDO]
  implicit val decoder: Decoder[BillFindingDO] = deriveDecoder[BillFindingDO]

}
