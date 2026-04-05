package repcheck.shared.models.congress.dos.bill

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillTextVersionDO(
  versionId: UUID,
  billId: String,
  versionCode: String,
  versionType: String,
  versionDate: Option[String],
  formatType: Option[String],
  url: Option[String],
  content: Option[String],
  embedding: Option[Array[Float]],
  fetchedAt: Option[Instant],
  createdAt: Option[Instant],
)

object BillTextVersionDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillTextVersionDO] = deriveEncoder[BillTextVersionDO]
  implicit val decoder: Decoder[BillTextVersionDO] = deriveDecoder[BillTextVersionDO]

}
