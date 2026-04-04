package repcheck.shared.models.congress.committee

import io.circe.{Decoder, Encoder}

final case class UnrecognizedCommitteePosition(value: String)
    extends Exception(
      s"Unrecognized CommitteePosition: '$value'. Valid values: Chairman, Chair, Chairwoman, Chairperson, RankingMember, Ranking, Ranking Member, ViceChairman, Vice Chair, Vice Chairwoman, Member"
    )

enum CommitteePosition(val apiValue: String, val weight: Double) {
  case Chairman      extends CommitteePosition("Chairman", 1.0)
  case RankingMember extends CommitteePosition("Ranking Member", 0.7)
  case ViceChairman  extends CommitteePosition("Vice Chairman", 0.6)
  case Member        extends CommitteePosition("Member", 0.4)
}

object CommitteePosition {

  private val aliases: Map[String, CommitteePosition] = Map(
    "CHAIRMAN"        -> CommitteePosition.Chairman,
    "CHAIR"           -> CommitteePosition.Chairman,
    "CHAIRWOMAN"      -> CommitteePosition.Chairman,
    "CHAIRPERSON"     -> CommitteePosition.Chairman,
    "RANKING MEMBER"  -> CommitteePosition.RankingMember,
    "RANKINGMEMBER"   -> CommitteePosition.RankingMember,
    "RANKING"         -> CommitteePosition.RankingMember,
    "VICE CHAIRMAN"   -> CommitteePosition.ViceChairman,
    "VICECHAIRMAN"    -> CommitteePosition.ViceChairman,
    "VICE CHAIR"      -> CommitteePosition.ViceChairman,
    "VICE CHAIRWOMAN" -> CommitteePosition.ViceChairman,
    "MEMBER"          -> CommitteePosition.Member,
  )

  def fromString(value: String): Either[UnrecognizedCommitteePosition, CommitteePosition] =
    aliases.get(value.toUpperCase) match {
      case Some(cp) => Right(cp)
      case None     => Left(UnrecognizedCommitteePosition(value))
    }

  implicit val encoder: Encoder[CommitteePosition] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[CommitteePosition] = Decoder.decodeString.emap { str =>
    fromString(str).left.map(_.getMessage)
  }

}
