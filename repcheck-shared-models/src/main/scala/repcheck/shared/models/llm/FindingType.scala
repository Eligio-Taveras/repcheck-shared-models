package repcheck.shared.models.llm

import io.circe.{Decoder, Encoder}

final case class UnrecognizedFindingType(value: String)
    extends Exception(
      s"Unrecognized FindingType: '$value'. Valid values: TopicExtraction, BillSummary, PolicyAnalysis, StanceDetection, Pork, Rider, Lobbying, Constitutional, TextVersionDiff, Impact, FiscalEstimate"
    )

enum FindingType(val apiValue: String) {
  case TopicExtraction extends FindingType("TopicExtraction")
  case BillSummary     extends FindingType("BillSummary")
  case PolicyAnalysis  extends FindingType("PolicyAnalysis")
  case StanceDetection extends FindingType("StanceDetection")
  case Pork            extends FindingType("Pork")
  case Rider           extends FindingType("Rider")
  case Lobbying        extends FindingType("Lobbying")
  case Constitutional  extends FindingType("Constitutional")
  case TextVersionDiff extends FindingType("TextVersionDiff")
  case Impact          extends FindingType("Impact")
  case FiscalEstimate  extends FindingType("FiscalEstimate")
}

object FindingType {

  private val aliases: Map[String, FindingType] =
    FindingType.values.map(ft => ft.toString.toUpperCase -> ft).toMap

  def fromString(value: String): Either[UnrecognizedFindingType, FindingType] =
    aliases.get(value.toUpperCase) match {
      case Some(ft) => Right(ft)
      case None     => Left(UnrecognizedFindingType(value))
    }

  implicit val encoder: Encoder[FindingType] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[FindingType] = Decoder.decodeString.emap { str =>
    fromString(str).left.map(_.getMessage)
  }

}
