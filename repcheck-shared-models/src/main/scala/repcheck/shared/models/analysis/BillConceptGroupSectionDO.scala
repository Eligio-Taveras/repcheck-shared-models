package repcheck.shared.models.analysis

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillConceptGroupSectionDO(
  conceptGroupId: Long,
  sectionId: Long,
)

object BillConceptGroupSectionDO {

  implicit val encoder: Encoder[BillConceptGroupSectionDO] = deriveEncoder[BillConceptGroupSectionDO]
  implicit val decoder: Decoder[BillConceptGroupSectionDO] = deriveDecoder[BillConceptGroupSectionDO]

}
