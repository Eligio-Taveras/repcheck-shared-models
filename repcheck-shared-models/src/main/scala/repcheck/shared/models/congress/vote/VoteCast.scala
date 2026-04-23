package repcheck.shared.models.congress.vote

import io.circe.{Decoder, Encoder}

final case class UnrecognizedVoteCast(value: String)
    extends Exception(
      s"Unrecognized VoteCast: '$value'. Valid values: Yea, Aye, Yes, Nay, No, Present, NotVoting, Not Voting, Absent, Candidate"
    )

enum VoteCast(val apiValue: String) {
  case Yea       extends VoteCast("Yea")
  case Nay       extends VoteCast("Nay")
  case Present   extends VoteCast("Present")
  case NotVoting extends VoteCast("Not Voting")
  case Absent    extends VoteCast("Absent")

  /**
   * Member voted for a specific candidate in an officer-election vote (House Speaker, Clerk, etc.). The candidate's
   * name is NOT carried on the enum — it lives in the companion `vote_positions.vote_cast_candidate_name` column per DB
   * migration 025 (and on `VotePositionDO.voteCastCandidateName`). Using the DB column rather than a parameterized enum
   * case keeps `vote_cast_type` a flat SQL enum, which plays nicely with Doobie's `pgEnumStringOpt`.
   */
  case Candidate extends VoteCast("Candidate")
}

object VoteCast {

  private val aliases: Map[String, VoteCast] = Map(
    "YEA"        -> VoteCast.Yea,
    "AYE"        -> VoteCast.Yea,
    "YES"        -> VoteCast.Yea,
    "NAY"        -> VoteCast.Nay,
    "NO"         -> VoteCast.Nay,
    "PRESENT"    -> VoteCast.Present,
    "NOT VOTING" -> VoteCast.NotVoting,
    "NOTVOTING"  -> VoteCast.NotVoting,
    "ABSENT"     -> VoteCast.Absent,
    "CANDIDATE"  -> VoteCast.Candidate,
  )

  def fromString(value: String): Either[UnrecognizedVoteCast, VoteCast] =
    aliases.get(value.toUpperCase) match {
      case Some(vc) => Right(vc)
      case None     => Left(UnrecognizedVoteCast(value))
    }

  implicit val encoder: Encoder[VoteCast] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[VoteCast] = Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
