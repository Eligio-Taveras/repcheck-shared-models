package repcheck.shared.models.llm

import io.circe.{Decoder, Encoder}

final case class UnrecognizedImpactSeverity(value: String)
    extends Exception(
      s"Unrecognized ImpactSeverity: '$value'. Valid values: High, Medium, Low"
    )

enum ImpactSeverity(val apiValue: String) {
  case High   extends ImpactSeverity("high")
  case Medium extends ImpactSeverity("medium")
  case Low    extends ImpactSeverity("low")
}

object ImpactSeverity {

  private val lookup: Map[String, ImpactSeverity] = {
    val byName = ImpactSeverity.values.map(is => is.toString.toUpperCase -> is).toMap
    val byApi  = ImpactSeverity.values.map(is => is.apiValue.toUpperCase -> is).toMap
    byName ++ byApi
  }

  def fromString(value: String): Either[UnrecognizedImpactSeverity, ImpactSeverity] =
    lookup.get(value.toUpperCase) match {
      case Some(is) => Right(is)
      case None     => Left(UnrecognizedImpactSeverity(value))
    }

  implicit val encoder: Encoder[ImpactSeverity] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[ImpactSeverity] =
    Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
