package repcheck.shared.models.congress.vote

import io.circe.{Decoder, Encoder}

final case class UnrecognizedVoteMethod(value: String)
    extends Exception(
      s"Unrecognized VoteMethod: '$value'. Valid values: recorded vote, voice vote, unanimous consent, roll, " +
        "yea-and-nay, 2/3 yea-and-nay, quorum call"
    )

enum VoteMethod(val apiValue: String) {
  case RecordedVote       extends VoteMethod("recorded vote")
  case VoiceVote          extends VoteMethod("voice vote")
  case UnanimousConsent   extends VoteMethod("unanimous consent")
  case Roll               extends VoteMethod("roll")
  case YeaAndNay          extends VoteMethod("yea-and-nay")
  case TwoThirdsYeaAndNay extends VoteMethod("2/3 yea-and-nay")
  case QuorumCall         extends VoteMethod("quorum call")
}

object VoteMethod {

  /**
   * Synonyms returned by the Congress.gov House-vote API that map onto our existing canonical [[VoteMethod]] cases. The
   * Clerk's Office occasionally publishes the same procedural mechanism under multiple names — e.g. `"2/3 Recorded
   * Vote"` is the modern label for the historical `"2/3 yea-and-nay"` (named vote requiring a two-thirds supermajority,
   * used for veto overrides + suspension of the rules), and bare `"Quorum"` is a House-clerk shorthand for what
   * `"quorum call"` covers (a procedural vote whose only purpose is to establish whether a quorum is present).
   *
   * Adding the aliases here instead of new enum cases keeps the domain model lean: the underlying procedure is the
   * same, only the label differs by era. If a future caller needs to distinguish the exact API string, the raw
   * `voteMethod` field in the source DTO is still preserved upstream — this normalization happens only at the DTO→DO
   * boundary.
   *
   * Surfaced live: `2/3 Recorded Vote` on 119-House-1-289, `Quorum` on 119-House-1-1 + 119-House-2-1 during the
   * post-`HOUSE_LOOKBACK_DAYS=0` backfill in P6 docker-compose validation.
   */
  private val houseSynonyms: Map[String, VoteMethod] = Map(
    "2/3 RECORDED VOTE" -> TwoThirdsYeaAndNay,
    "QUORUM"            -> QuorumCall,
  )

  private val aliases: Map[String, VoteMethod] = {
    val byApi  = VoteMethod.values.map(vm => vm.apiValue.toUpperCase -> vm).toMap
    val byName = VoteMethod.values.map(vm => vm.toString.toUpperCase -> vm).toMap
    byApi ++ byName ++ houseSynonyms
  }

  def fromString(value: String): Either[UnrecognizedVoteMethod, VoteMethod] =
    aliases.get(value.toUpperCase) match {
      case Some(vm) => Right(vm)
      case None     => Left(UnrecognizedVoteMethod(value))
    }

  implicit val encoder: Encoder[VoteMethod] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[VoteMethod] = Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
