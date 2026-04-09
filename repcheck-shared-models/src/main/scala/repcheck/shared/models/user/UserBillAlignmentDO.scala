package repcheck.shared.models.user

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.codecs.VectorCodec._

final case class UserBillAlignmentDO(
  userId: UUID,
  billId: Long,
  topic: String,
  userStanceScore: Double,
  billStanceDirection: String,
  alignmentScore: Double,
  reasoning: Option[String],
  reasoningEmbedding: Option[Array[Float]],
  findingId: Option[Long],
  computedAt: Option[Instant],
)

object UserBillAlignmentDO {

  implicit val encoder: Encoder[UserBillAlignmentDO] = deriveEncoder[UserBillAlignmentDO]
  implicit val decoder: Decoder[UserBillAlignmentDO] = deriveDecoder[UserBillAlignmentDO]

}
