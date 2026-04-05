package repcheck.shared.models.congress.bill

import io.circe.{Decoder, Encoder}

final case class UnrecognizedChangeType(value: String)
    extends Exception(
      s"Unrecognized ChangeType: '$value'. Valid values: Added, Removed, Modified, Renumbered"
    )

enum ChangeType(val apiValue: String) {
  case Added      extends ChangeType("added")
  case Removed    extends ChangeType("removed")
  case Modified   extends ChangeType("modified")
  case Renumbered extends ChangeType("renumbered")
}

object ChangeType {

  private val aliases: Map[String, ChangeType] = Map(
    "ADDED"      -> ChangeType.Added,
    "REMOVED"    -> ChangeType.Removed,
    "MODIFIED"   -> ChangeType.Modified,
    "RENUMBERED" -> ChangeType.Renumbered,
  )

  def fromString(value: String): Either[UnrecognizedChangeType, ChangeType] =
    aliases.get(value.toUpperCase) match {
      case Some(ct) => Right(ct)
      case None     => Left(UnrecognizedChangeType(value))
    }

  implicit val encoder: Encoder[ChangeType] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[ChangeType] = Decoder.decodeString.emap { str =>
    fromString(str).left.map(_.getMessage)
  }

}
