package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class BillAnalysisDO(
  id: Long,
  billId: Long,
  versionId: Long,
  status: String,
  summary: Option[String],
  topics: List[String],
  readingLevel: Option[String],
  keyPoints: List[String],
  passesExecuted: List[Int],
  highestModelUsed: Option[String],
  pass1Model: Option[String],
  pass2Model: Option[String],
  pass3Model: Option[String],
  embedding: Option[Array[Float]],
  highProfileScore: Option[Double],
  mediaCoverageLevel: Option[Double],
  appropriationsEstimate: Option[BigDecimal],
  stanceConfidence: Option[Double],
  routingReasoning: Option[String],
  overallConfidence: Option[Double],
  crossConceptContradictionScore: Option[Double],
  expectedVoteContention: Option[Double],
  contradictionDetails: Option[String],
  routingReasoningPass2: Option[String],
  failureReason: Option[String],
  createdAt: Option[Instant],
  completedAt: Option[Instant],
)

object BillAnalysisDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillAnalysisDO] = deriveEncoder[BillAnalysisDO]
  implicit val decoder: Decoder[BillAnalysisDO] = deriveDecoder[BillAnalysisDO]

}
