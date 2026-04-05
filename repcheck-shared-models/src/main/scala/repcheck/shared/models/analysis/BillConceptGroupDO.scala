package repcheck.shared.models.analysis

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillConceptGroupDO(
  conceptGroupId: UUID,
  versionId: UUID,
  billId: String,
  groupId: String,
  title: String,
  simplifiedText: String,
  embedding: Option[Array[Float]],
  createdAt: Option[Instant],
)

object BillConceptGroupDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillConceptGroupDO] = deriveEncoder[BillConceptGroupDO]
  implicit val decoder: Decoder[BillConceptGroupDO] = deriveDecoder[BillConceptGroupDO]

}
