package repcheck.shared.models.congress.amendment

import io.circe.{Decoder, Encoder}

final case class UnrecognizedSponsorType(value: String)
    extends Exception(
      s"Unrecognized SponsorType: '$value'. Valid values: member, committee"
    )

enum SponsorType(val apiValue: String) {
  case Member    extends SponsorType("member")
  case Committee extends SponsorType("committee")
}

object SponsorType {

  private val aliases: Map[String, SponsorType] = Map(
    "MEMBER"    -> SponsorType.Member,
    "COMMITTEE" -> SponsorType.Committee,
  )

  def fromString(value: String): Either[UnrecognizedSponsorType, SponsorType] =
    aliases.get(value.toUpperCase) match {
      case Some(st) => Right(st)
      case None     => Left(UnrecognizedSponsorType(value))
    }

  implicit val encoder: Encoder[SponsorType] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[SponsorType] = Decoder.decodeString.emap { str =>
    fromString(str).left.map(_.getMessage)
  }

}
