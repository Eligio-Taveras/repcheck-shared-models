package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.llm.ImpactSeverity

final case class ImpactItem(
  affectedGroup: String,
  impactType: String,
  description: String,
  severity: ImpactSeverity,
)

object ImpactItem {

  implicit val encoder: Encoder[ImpactItem] = deriveEncoder[ImpactItem]
  implicit val decoder: Decoder[ImpactItem] = deriveDecoder[ImpactItem]

}

final case class ImpactAnalysisOutput(
  impacts: List[ImpactItem]
)

object ImpactAnalysisOutput {

  implicit val encoder: Encoder[ImpactAnalysisOutput] = deriveEncoder[ImpactAnalysisOutput]
  implicit val decoder: Decoder[ImpactAnalysisOutput] = deriveDecoder[ImpactAnalysisOutput]

}
