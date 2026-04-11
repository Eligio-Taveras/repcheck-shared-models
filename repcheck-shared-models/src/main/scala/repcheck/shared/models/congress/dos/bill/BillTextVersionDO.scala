package repcheck.shared.models.congress.dos.bill

import java.time.{Instant, LocalDate}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.common.FormatType

final case class BillTextVersionDO(
  id: Long,
  billId: Long,
  versionCode: String,
  versionType: String,
  versionDate: Option[LocalDate],
  formatType: Option[FormatType],
  url: Option[String],
  content: Option[String],
  embedding: Option[Array[Float]],
  fetchedAt: Option[Instant],
  createdAt: Option[Instant],
)

object BillTextVersionDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}
  import repcheck.shared.models.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

  implicit val encoder: Encoder[BillTextVersionDO] = deriveEncoder[BillTextVersionDO]
  implicit val decoder: Decoder[BillTextVersionDO] = deriveDecoder[BillTextVersionDO]

}
