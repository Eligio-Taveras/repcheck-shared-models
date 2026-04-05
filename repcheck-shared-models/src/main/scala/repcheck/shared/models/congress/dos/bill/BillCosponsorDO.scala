package repcheck.shared.models.congress.dos.bill

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillCosponsorDO(
  billId: String,
  memberId: String,
  isOriginalCosponsor: Option[Boolean],
  sponsorshipDate: Option[String],
)

object BillCosponsorDO {

  implicit val encoder: Encoder[BillCosponsorDO] = deriveEncoder[BillCosponsorDO]
  implicit val decoder: Decoder[BillCosponsorDO] = deriveDecoder[BillCosponsorDO]

}
