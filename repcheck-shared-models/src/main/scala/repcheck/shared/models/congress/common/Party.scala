package repcheck.shared.models.congress.common

import io.circe.{Decoder, Encoder}

final case class UnrecognizedParty(value: String)
    extends Exception(
      s"Unrecognized Party: '$value'. Valid values: Democrat, Democratic, D, Republican, R, Independent, I, ID"
    )

enum Party(val apiValue: String) {
  case Democrat    extends Party("Democrat")
  case Republican  extends Party("Republican")
  case Independent extends Party("Independent")
}

object Party {

  private val aliases: Map[String, Party] = Map(
    "DEMOCRAT"    -> Party.Democrat,
    "DEMOCRATIC"  -> Party.Democrat,
    "D"           -> Party.Democrat,
    "REPUBLICAN"  -> Party.Republican,
    "R"           -> Party.Republican,
    "INDEPENDENT" -> Party.Independent,
    "I"           -> Party.Independent,
    "ID"          -> Party.Independent,
  )

  def fromString(value: String): Either[UnrecognizedParty, Party] =
    aliases.get(value.toUpperCase) match {
      case Some(p) => Right(p)
      case None    => Left(UnrecognizedParty(value))
    }

  implicit val encoder: Encoder[Party] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[Party] = Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
