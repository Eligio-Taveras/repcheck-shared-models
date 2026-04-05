package repcheck.shared.models.user

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.codecs.VectorCodec._

final case class MemberBillStanceTopicDO(
  id: UUID,
  memberId: String,
  billId: String,
  voteId: Option[String],
  topic: String,
  stanceDirection: String,
  reasoning: Option[String],
  reasoningEmbedding: Option[Array[Float]],
  findingId: Option[UUID],
  confidence: Option[Double],
  conceptSummary: Option[String],
  createdAt: Option[Instant],
)

object MemberBillStanceTopicDO {

  implicit val encoder: Encoder[MemberBillStanceTopicDO] = deriveEncoder[MemberBillStanceTopicDO]
  implicit val decoder: Decoder[MemberBillStanceTopicDO] = deriveDecoder[MemberBillStanceTopicDO]

}
