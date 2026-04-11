package repcheck.shared.models.congress.dos.bill

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillSubjectDO(
  billId: Long,
  subjectName: String,
  embedding: Option[Array[Float]],
  updateDate: Option[Instant],
)

object BillSubjectDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillSubjectDO] = deriveEncoder[BillSubjectDO]
  implicit val decoder: Decoder[BillSubjectDO] = deriveDecoder[BillSubjectDO]

}
