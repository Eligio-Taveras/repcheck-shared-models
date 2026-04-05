package repcheck.shared.models.analysis

import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillConceptGroupSectionDO(
  conceptGroupId: UUID,
  sectionId: UUID,
)

object BillConceptGroupSectionDO {

  implicit val encoder: Encoder[BillConceptGroupSectionDO] = deriveEncoder[BillConceptGroupSectionDO]
  implicit val decoder: Decoder[BillConceptGroupSectionDO] = deriveDecoder[BillConceptGroupSectionDO]

}
