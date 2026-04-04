package repcheck.shared.models.congress.amendment

import io.circe.{Decoder, Encoder}

final case class UnrecognizedAmendmentType(value: String)
    extends Exception(
      s"Unrecognized AmendmentType: '$value'. Valid values: HAMDT, SAMDT, SUAMDT"
    )

enum AmendmentType(val apiValue: String) {
  case HAMDT  extends AmendmentType("hamdt")
  case SAMDT  extends AmendmentType("samdt")
  case SUAMDT extends AmendmentType("suamdt")
}

object AmendmentType {

  private val aliases: Map[String, AmendmentType] = Map(
    "HAMDT"  -> AmendmentType.HAMDT,
    "SAMDT"  -> AmendmentType.SAMDT,
    "SUAMDT" -> AmendmentType.SUAMDT,
  )

  def fromString(value: String): Either[UnrecognizedAmendmentType, AmendmentType] =
    aliases.get(value.toUpperCase) match {
      case Some(at) => Right(at)
      case None     => Left(UnrecognizedAmendmentType(value))
    }

  implicit val encoder: Encoder[AmendmentType] =
    Encoder.encodeString.contramap(_.toString)

  implicit val decoder: Decoder[AmendmentType] = Decoder.decodeString.emap { str =>
    fromString(str).left.map(_.getMessage)
  }

}
