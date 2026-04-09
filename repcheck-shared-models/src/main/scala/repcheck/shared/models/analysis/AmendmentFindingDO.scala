package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class AmendmentFindingDO(
  id: Long,
  amendmentId: Long,
  findingTypeId: Int,
  summary: String,
  details: Option[String],
  severity: Option[String],
  confidence: Option[Float],
  affectedSection: Option[String],
  embedding: Option[Array[Float]],
  llmModel: String,
  analyzedAt: Option[Instant],
)

object AmendmentFindingDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[AmendmentFindingDO] = deriveEncoder[AmendmentFindingDO]
  implicit val decoder: Decoder[AmendmentFindingDO] = deriveDecoder[AmendmentFindingDO]

}
