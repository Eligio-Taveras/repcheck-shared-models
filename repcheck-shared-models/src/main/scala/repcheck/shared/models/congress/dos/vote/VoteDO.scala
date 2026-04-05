package repcheck.shared.models.congress.dos.vote

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class VoteDO(
  voteId: String,
  congress: Int,
  chamber: String,
  rollNumber: Int,
  sessionNumber: Option[Int],
  billId: Option[String],
  question: Option[String],
  voteType: Option[String],
  result: Option[String],
  voteDate: Option[String],
  legislationNumber: Option[String],
  legislationType: Option[String],
  legislationUrl: Option[String],
  sourceDataUrl: Option[String],
  updateDate: Option[String],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object VoteDO {

  implicit val encoder: Encoder[VoteDO] = deriveEncoder[VoteDO]
  implicit val decoder: Decoder[VoteDO] = deriveDecoder[VoteDO]

}
