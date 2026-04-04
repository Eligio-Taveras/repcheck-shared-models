package repcheck.shared.models.congress.vote

import io.circe.{Decoder, Encoder}

final case class UnrecognizedVoteType(value: String)
    extends Exception(
      s"Unrecognized VoteType: '$value'. Valid values: Passage, ConferenceReport, Cloture, VetoOverride, Amendment, Committee, Recommit, Other"
    )

enum VoteType(val apiValue: String) {
  case Passage          extends VoteType("Passage")
  case ConferenceReport extends VoteType("Conference Report")
  case Cloture          extends VoteType("Cloture")
  case VetoOverride     extends VoteType("Veto Override")
  case Amendment        extends VoteType("Amendment")
  case Committee        extends VoteType("Committee")
  case Recommit         extends VoteType("Recommit")
  case Other            extends VoteType("Other")
}

object VoteType {

  private val aliases: Map[String, VoteType] = Map(
    "PASSAGE"           -> VoteType.Passage,
    "CONFERENCE REPORT" -> VoteType.ConferenceReport,
    "CONFERENCEREPORT"  -> VoteType.ConferenceReport,
    "CLOTURE"           -> VoteType.Cloture,
    "VETO OVERRIDE"     -> VoteType.VetoOverride,
    "VETOOVERRIDE"      -> VoteType.VetoOverride,
    "AMENDMENT"         -> VoteType.Amendment,
    "COMMITTEE"         -> VoteType.Committee,
    "RECOMMIT"          -> VoteType.Recommit,
    "OTHER"             -> VoteType.Other,
  )

  def fromString(value: String): Either[UnrecognizedVoteType, VoteType] =
    aliases.get(value.toUpperCase) match {
      case Some(vt) => Right(vt)
      case None     => Left(UnrecognizedVoteType(value))
    }

  def fromQuestion(question: String): VoteType = {
    val upper = question.toUpperCase
    if (upper.contains("ON PASSAGE")) {
      VoteType.Passage
    } else if (upper.contains("CONFERENCE REPORT")) {
      VoteType.ConferenceReport
    } else if (upper.contains("ON CLOTURE") || upper.contains("CLOTURE")) {
      VoteType.Cloture
    } else if (upper.contains("VETO")) {
      VoteType.VetoOverride
    } else if (upper.contains("AMENDMENT")) {
      VoteType.Amendment
    } else if (upper.contains("COMMITTEE")) {
      VoteType.Committee
    } else if (upper.contains("RECOMMIT")) {
      VoteType.Recommit
    } else {
      VoteType.Other
    }
  }

  implicit val encoder: Encoder[VoteType] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[VoteType] = Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
