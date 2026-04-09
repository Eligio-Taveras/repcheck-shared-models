package repcheck.shared.models.user

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.codecs.VectorCodec._

final case class MemberBillStanceTopicDO(
  id: Long,
  memberId: Long,
  billId: Long,
  voteId: Option[Long],
  topic: String,
  stanceDirection: String,
  reasoning: Option[String],
  reasoningEmbedding: Option[Array[Float]],
  findingId: Option[Long],
  confidence: Option[Double],
  conceptSummary: Option[String],
  createdAt: Option[Instant],
)

object MemberBillStanceTopicDO {

  implicit val encoder: Encoder[MemberBillStanceTopicDO] = deriveEncoder[MemberBillStanceTopicDO]
  implicit val decoder: Decoder[MemberBillStanceTopicDO] = deriveDecoder[MemberBillStanceTopicDO]

}
