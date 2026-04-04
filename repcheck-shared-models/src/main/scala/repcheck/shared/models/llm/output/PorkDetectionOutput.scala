package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.llm.{ImpactSeverity, PorkType}

final case class PorkFinding(
  porkType: PorkType,
  description: String,
  affectedSection: String,
  severity: ImpactSeverity,
)

object PorkFinding {

  implicit val encoder: Encoder[PorkFinding] = deriveEncoder[PorkFinding]
  implicit val decoder: Decoder[PorkFinding] = deriveDecoder[PorkFinding]

}

final case class PorkDetectionOutput(
  findings: List[PorkFinding]
)

object PorkDetectionOutput {

  implicit val encoder: Encoder[PorkDetectionOutput] = deriveEncoder[PorkDetectionOutput]
  implicit val decoder: Decoder[PorkDetectionOutput] = deriveDecoder[PorkDetectionOutput]

}
