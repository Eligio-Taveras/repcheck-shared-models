package repcheck.shared.models.user.score

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.codecs.DoobieArrayCodecs._
import repcheck.shared.models.codecs.VectorCodec._

final case class ScoreDO(
  userId: UUID,
  memberId: String,
  aggregateScore: Float,
  status: String,
  lastUpdated: Option[Instant],
  llmModel: Option[String],
  totalBills: Option[Int],
  totalVotes: Option[Int],
  nonOverlappingTopics: List[String],
  reasoning: Option[String],
  reasoningEmbedding: Option[Array[Float]],
)

object ScoreDO {

  implicit val encoder: Encoder[ScoreDO] = deriveEncoder[ScoreDO]
  implicit val decoder: Decoder[ScoreDO] = deriveDecoder[ScoreDO]

}
