package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import org.scalacheck.Gen
import repcheck.shared.models.llm.codec.StructuredCodec
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

  private val example =
    PorkDetectionOutput(List(PorkFinding(PorkType.Earmark, "Funds a local bridge.", "Sec. 5", ImpactSeverity.Medium)))

  private val gen: Gen[PorkDetectionOutput] = {
    val findingGen = for {
      porkType    <- Gen.oneOf(PorkType.values.toIndexedSeq)
      description <- Gen.alphaNumStr
      section     <- Gen.alphaNumStr
      severity    <- Gen.oneOf(ImpactSeverity.values.toIndexedSeq)
    } yield PorkFinding(porkType, description, section, severity)
    for {
      n        <- Gen.choose(0, 4)
      findings <- Gen.listOfN(n, findingGen)
    } yield PorkDetectionOutput(findings)
  }

  given StructuredCodec[PorkDetectionOutput] =
    StructuredCodec.instance(AnalysisTapirSchemas.porkDetectionOutput, example, gen)

}
