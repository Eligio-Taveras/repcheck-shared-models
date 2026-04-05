package repcheck.shared.models.analysis

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class FindingTypeDO(
  findingTypeId: Int,
  code: String,
  name: String,
  description: Option[String],
)

object FindingTypeDO {

  implicit val encoder: Encoder[FindingTypeDO] = deriveEncoder[FindingTypeDO]
  implicit val decoder: Decoder[FindingTypeDO] = deriveDecoder[FindingTypeDO]

}
