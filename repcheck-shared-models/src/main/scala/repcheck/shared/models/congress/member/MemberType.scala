package repcheck.shared.models.congress.member

import io.circe.{Decoder, Encoder}

final case class UnrecognizedMemberType(value: String)
    extends Exception(
      s"Unrecognized MemberType: '$value'. Valid values: Representative, rep, Senator, sen, Delegate, del, Resident Commissioner"
    )

enum MemberType(val apiValue: String) {
  case Representative       extends MemberType("Representative")
  case Senator              extends MemberType("Senator")
  case Delegate             extends MemberType("Delegate")
  case ResidentCommissioner extends MemberType("Resident Commissioner")
}

object MemberType {

  private val aliases: Map[String, MemberType] = Map(
    "REPRESENTATIVE"        -> MemberType.Representative,
    "REP"                   -> MemberType.Representative,
    "SENATOR"               -> MemberType.Senator,
    "SEN"                   -> MemberType.Senator,
    "DELEGATE"              -> MemberType.Delegate,
    "DEL"                   -> MemberType.Delegate,
    "RESIDENT COMMISSIONER" -> MemberType.ResidentCommissioner,
  )

  def fromString(value: String): Either[UnrecognizedMemberType, MemberType] =
    aliases.get(value.toUpperCase) match {
      case Some(mt) => Right(mt)
      case None     => Left(UnrecognizedMemberType(value))
    }

  implicit val encoder: Encoder[MemberType] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[MemberType] = Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
