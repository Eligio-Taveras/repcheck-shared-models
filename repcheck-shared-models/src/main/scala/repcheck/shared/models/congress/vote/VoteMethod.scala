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

  private val aliases: Map[String, VoteMethod] = {
    val byApi  = VoteMethod.values.map(vm => vm.apiValue.toUpperCase -> vm).toMap
    val byName = VoteMethod.values.map(vm => vm.toString.toUpperCase -> vm).toMap
    byApi ++ byName
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
