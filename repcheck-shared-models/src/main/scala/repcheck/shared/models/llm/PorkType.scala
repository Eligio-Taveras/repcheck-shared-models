package repcheck.shared.models.llm

import io.circe.{Decoder, Encoder}

final case class UnrecognizedPorkType(value: String)
    extends Exception(
      s"Unrecognized PorkType: '$value'. Valid values: Earmark, Rider, UnrelatedProvision"
    )

enum PorkType(val apiValue: String) {
  case Earmark            extends PorkType("earmark")
  case Rider              extends PorkType("rider")
  case UnrelatedProvision extends PorkType("unrelatedprovision")
}

object PorkType {

  private val lookup: Map[String, PorkType] = {
    val byName  = PorkType.values.map(pt => pt.toString.toUpperCase -> pt).toMap
    val byApi   = PorkType.values.map(pt => pt.apiValue.toUpperCase -> pt).toMap
    val aliases = Map(
      "UNRELATED_PROVISION" -> PorkType.UnrelatedProvision,
      "UNRELATED-PROVISION" -> PorkType.UnrelatedProvision
    )
    byName ++ byApi ++ aliases
  }

  def fromString(value: String): Either[UnrecognizedPorkType, PorkType] =
    lookup.get(value.toUpperCase) match {
      case Some(pt) => Right(pt)
      case None     => Left(UnrecognizedPorkType(value))
    }

  implicit val encoder: Encoder[PorkType] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[PorkType] =
    Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
