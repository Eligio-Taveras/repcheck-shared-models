package repcheck.shared.models.congress.committee

import io.circe.{Decoder, Encoder}

final case class UnrecognizedCommitteeType(value: String)
    extends Exception(
      s"Unrecognized CommitteeType: '$value'. Valid values: Standing, Special, Select, Joint, Subcommittee"
    )

enum CommitteeType(val apiValue: String) {
  case Standing     extends CommitteeType("Standing")
  case Special      extends CommitteeType("Special")
  case Select       extends CommitteeType("Select")
  case Joint        extends CommitteeType("Joint")
  case Subcommittee extends CommitteeType("Subcommittee")
}

object CommitteeType {

  private val aliases: Map[String, CommitteeType] = Map(
    "STANDING"     -> CommitteeType.Standing,
    "SPECIAL"      -> CommitteeType.Special,
    "SELECT"       -> CommitteeType.Select,
    "JOINT"        -> CommitteeType.Joint,
    "SUBCOMMITTEE" -> CommitteeType.Subcommittee,
  )

  def fromString(value: String): Either[UnrecognizedCommitteeType, CommitteeType] =
    aliases.get(value.toUpperCase) match {
      case Some(ct) => Right(ct)
      case None     => Left(UnrecognizedCommitteeType(value))
    }

  implicit val encoder: Encoder[CommitteeType] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[CommitteeType] = Decoder.decodeString.emap { str =>
    fromString(str).left.map(_.getMessage)
  }

}
