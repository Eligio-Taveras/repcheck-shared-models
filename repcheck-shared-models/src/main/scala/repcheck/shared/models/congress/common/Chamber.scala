package repcheck.shared.models.congress.common

import io.circe.{Decoder, Encoder}

final case class UnrecognizedChamber(value: String)
    extends Exception(
      s"Unrecognized Chamber: '$value'. Valid values: House, Senate, House of Representatives"
    )

enum Chamber(val apiValue: String) {
  case House  extends Chamber("House")
  case Senate extends Chamber("Senate")
}

object Chamber {

  private val aliases: Map[String, Chamber] = Map(
    "HOUSE"                    -> Chamber.House,
    "HOUSE OF REPRESENTATIVES" -> Chamber.House,
    "SENATE"                   -> Chamber.Senate,
  )

  def fromString(value: String): Either[UnrecognizedChamber, Chamber] =
    aliases.get(value.toUpperCase) match {
      case Some(c) => Right(c)
      case None    => Left(UnrecognizedChamber(value))
    }

  implicit val encoder: Encoder[Chamber] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[Chamber] = Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
