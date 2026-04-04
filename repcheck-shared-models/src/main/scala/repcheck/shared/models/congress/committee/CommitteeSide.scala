package repcheck.shared.models.congress.committee

import io.circe.{Decoder, Encoder}

final case class UnrecognizedCommitteeSide(value: String)
    extends Exception(
      s"Unrecognized CommitteeSide: '$value'. Valid values: Majority, M, Minority"
    )

enum CommitteeSide(val apiValue: String) {
  case Majority extends CommitteeSide("Majority")
  case Minority extends CommitteeSide("Minority")
}

object CommitteeSide {

  private val aliases: Map[String, CommitteeSide] = Map(
    "MAJORITY" -> CommitteeSide.Majority,
    "M"        -> CommitteeSide.Majority,
    "MINORITY" -> CommitteeSide.Minority,
  )

  def fromString(value: String): Either[UnrecognizedCommitteeSide, CommitteeSide] =
    aliases.get(value.toUpperCase) match {
      case Some(cs) => Right(cs)
      case None     => Left(UnrecognizedCommitteeSide(value))
    }

  implicit val encoder: Encoder[CommitteeSide] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[CommitteeSide] = Decoder.decodeString.emap { str =>
    fromString(str).left.map(_.getMessage)
  }

}
