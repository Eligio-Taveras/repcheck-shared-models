package repcheck.shared.models.congress.dto.vote

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class VoteResultDTO(
  memberId: Option[String],
  firstName: Option[String],
  lastName: Option[String],
  voteCast: Option[String],
  party: Option[String],
  state: Option[String],
)

object VoteResultDTO {
  implicit val encoder: Encoder[VoteResultDTO] = deriveEncoder[VoteResultDTO]
  implicit val decoder: Decoder[VoteResultDTO] = deriveDecoder[VoteResultDTO]
}

final case class VotePartyTotalDTO(
  voteParty: Option[String],
  party: Option[String],
  yeaTotal: Option[Int],
  nayTotal: Option[Int],
  presentTotal: Option[Int],
  notVotingTotal: Option[Int],
)

object VotePartyTotalDTO {
  implicit val encoder: Encoder[VotePartyTotalDTO] = deriveEncoder[VotePartyTotalDTO]
  implicit val decoder: Decoder[VotePartyTotalDTO] = deriveDecoder[VotePartyTotalDTO]
}

final case class VoteListItemDTO(
  congress: Int,
  chamber: String,
  rollCallNumber: Int,
  sessionNumber: Option[Int],
  startDate: Option[String],
  updateDate: Option[String],
  result: Option[String],
  voteType: Option[String],
  legislationNumber: Option[String],
  legislationType: Option[String],
  legislationUrl: Option[String],
  url: Option[String],
  identifier: Option[String],
  sourceDataUrl: Option[String],
)

object VoteListItemDTO {
  implicit val encoder: Encoder[VoteListItemDTO] = deriveEncoder[VoteListItemDTO]
  implicit val decoder: Decoder[VoteListItemDTO] = deriveDecoder[VoteListItemDTO]
}

final case class VoteDetailDTO(
  congress: Int,
  chamber: String,
  rollCallNumber: Int,
  sessionNumber: Option[Int],
  startDate: Option[String],
  updateDate: Option[String],
  result: Option[String],
  voteType: Option[String],
  legislationNumber: Option[String],
  legislationType: Option[String],
  legislationUrl: Option[String],
  url: Option[String],
  voteQuestion: Option[String],
  votePartyTotal: Option[List[VotePartyTotalDTO]],
)

object VoteDetailDTO {
  implicit val encoder: Encoder[VoteDetailDTO] = deriveEncoder[VoteDetailDTO]
  implicit val decoder: Decoder[VoteDetailDTO] = deriveDecoder[VoteDetailDTO]
}

final case class VoteMembersDTO(
  congress: Int,
  chamber: String,
  rollCallNumber: Int,
  sessionNumber: Option[Int],
  startDate: Option[String],
  updateDate: Option[String],
  result: Option[String],
  voteType: Option[String],
  legislationNumber: Option[String],
  legislationType: Option[String],
  legislationUrl: Option[String],
  url: Option[String],
  voteQuestion: Option[String],
  results: Option[List[VoteResultDTO]],
)

object VoteMembersDTO {
  implicit val encoder: Encoder[VoteMembersDTO] = deriveEncoder[VoteMembersDTO]
  implicit val decoder: Decoder[VoteMembersDTO] = deriveDecoder[VoteMembersDTO]
}
