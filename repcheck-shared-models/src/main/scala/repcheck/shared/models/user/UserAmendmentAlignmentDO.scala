package repcheck.shared.models.user

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.codecs.VectorCodec._

final case class UserAmendmentAlignmentDO(
  userId: UUID,
  amendmentId: String,
  billId: Option[String],
  topic: String,
  userStanceScore: Double,
  amendmentStanceDirection: String,
  alignmentScore: Double,
  reasoning: Option[String],
  reasoningEmbedding: Option[Array[Float]],
  findingId: Option[UUID],
  computedAt: Option[Instant],
)

object UserAmendmentAlignmentDO {

  implicit val encoder: Encoder[UserAmendmentAlignmentDO] = deriveEncoder[UserAmendmentAlignmentDO]
  implicit val decoder: Decoder[UserAmendmentAlignmentDO] = deriveDecoder[UserAmendmentAlignmentDO]

}
