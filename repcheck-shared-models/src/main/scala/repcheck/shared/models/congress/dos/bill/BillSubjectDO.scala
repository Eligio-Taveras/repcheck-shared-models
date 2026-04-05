package repcheck.shared.models.congress.dos.bill

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillSubjectDO(
  billId: String,
  subjectName: String,
  embedding: Option[Array[Float]],
  updateDate: Option[String],
)

object BillSubjectDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillSubjectDO] = deriveEncoder[BillSubjectDO]
  implicit val decoder: Decoder[BillSubjectDO] = deriveDecoder[BillSubjectDO]

}
