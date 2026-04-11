package repcheck.shared.models.congress.dos.bill

import java.time.LocalDate

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillCosponsorDO(
  billId: Long,
  memberId: Long,
  isOriginalCosponsor: Option[Boolean],
  sponsorshipDate: Option[LocalDate],
)

object BillCosponsorDO {

  import repcheck.shared.models.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

  implicit val encoder: Encoder[BillCosponsorDO] = deriveEncoder[BillCosponsorDO]
  implicit val decoder: Decoder[BillCosponsorDO] = deriveDecoder[BillCosponsorDO]

}
