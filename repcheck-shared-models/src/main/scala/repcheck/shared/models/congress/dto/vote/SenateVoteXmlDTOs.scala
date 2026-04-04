package repcheck.shared.models.congress.dto.vote

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class SenateVoteMemberXmlDTO(
  lisMemberId: String,
  firstName: String,
  lastName: String,
  party: String,
  state: String,
  voteCast: String,
)

object SenateVoteMemberXmlDTO {
  implicit val encoder: Encoder[SenateVoteMemberXmlDTO] = deriveEncoder[SenateVoteMemberXmlDTO]
  implicit val decoder: Decoder[SenateVoteMemberXmlDTO] = deriveDecoder[SenateVoteMemberXmlDTO]
}

final case class SenateVoteXmlDTO(
  congress: Int,
  session: Int,
  voteNumber: Int,
  question: String,
  voteDate: String,
  result: String,
  members: List[SenateVoteMemberXmlDTO],
)

object SenateVoteXmlDTO {
  implicit val encoder: Encoder[SenateVoteXmlDTO] = deriveEncoder[SenateVoteXmlDTO]
  implicit val decoder: Decoder[SenateVoteXmlDTO] = deriveDecoder[SenateVoteXmlDTO]
}

final case class SenateMemberContactDTO(
  bioguideId: String,
  firstName: String,
  lastName: String,
  party: String,
  state: String,
  senateClass: Option[Int],
  address: Option[String],
  phone: Option[String],
  website: Option[String],
)

object SenateMemberContactDTO {
  implicit val encoder: Encoder[SenateMemberContactDTO] = deriveEncoder[SenateMemberContactDTO]
  implicit val decoder: Decoder[SenateMemberContactDTO] = deriveDecoder[SenateMemberContactDTO]
}
