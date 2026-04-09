package repcheck.shared.models.congress.dos.bill

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillTextSectionDO(
  id: Long,
  versionId: Long,
  billId: Long,
  sectionIndex: Int,
  sectionIdentifier: Option[String],
  heading: Option[String],
  content: String,
  embedding: Option[Array[Float]],
  createdAt: Option[Instant],
)

object BillTextSectionDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillTextSectionDO] = deriveEncoder[BillTextSectionDO]
  implicit val decoder: Decoder[BillTextSectionDO] = deriveDecoder[BillTextSectionDO]

}
