package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import org.scalacheck.Gen
import repcheck.shared.models.llm.ImpactSeverity
import repcheck.shared.models.llm.codec.StructuredCodec

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

  private val example =
    ImpactAnalysisOutput(
      List(ImpactItem("small businesses", "compliance cost", "New reporting burden.", ImpactSeverity.Low))
    )

  private val gen: Gen[ImpactAnalysisOutput] = {
    val itemGen = for {
      group       <- Gen.alphaNumStr
      impactType  <- Gen.alphaNumStr
      description <- Gen.alphaNumStr
      severity    <- Gen.oneOf(ImpactSeverity.values.toIndexedSeq)
    } yield ImpactItem(group, impactType, description, severity)
    for {
      n       <- Gen.choose(0, 4)
      impacts <- Gen.listOfN(n, itemGen)
    } yield ImpactAnalysisOutput(impacts)
  }

  given StructuredCodec[ImpactAnalysisOutput] =
    StructuredCodec.instance(AnalysisTapirSchemas.impactAnalysisOutput, example, gen)

}
