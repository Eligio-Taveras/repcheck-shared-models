package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * A concept group within a bill version: a cluster of sections about the same thing. `label` + `conceptSummary` are the
 * LLM-extracted concept; `embedding` is the embedding of that summary. `runId` is the producing decomposition run
 * (`bill_decomposition_runs`). A bill version is self-contained, so the group is reused per `versionId` alone — no
 * snapshot dimension. `taxonomyVersion` is legacy (the taxonomy approach, superseded by vectors-primary; NULL going
 * forward).
 */
final case class BillConceptGroupDO(
  id: Long,
  versionId: Long,
  billId: Long,
  label: String,
  conceptSummary: String,
  embedding: Option[Array[Float]],
  taxonomyVersion: Option[Int],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
  runId: Option[Long],
)

object BillConceptGroupDO {

  import com.repcheck.utils.doobie.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillConceptGroupDO] = deriveEncoder[BillConceptGroupDO]
  implicit val decoder: Decoder[BillConceptGroupDO] = deriveDecoder[BillConceptGroupDO]

}
