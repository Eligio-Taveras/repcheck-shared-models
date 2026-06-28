package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * A concept group within a bill version: a cluster of sections about the same thing. `label` + `conceptSummary` are the
 * LLM-extracted concept; `embedding` is the embedding of that summary. `decompositionSnapshotVersion` is the SNAP
 * snapshot this group was produced under (the reuse-check / idempotency dimension) and `runId` the producing
 * decomposition run (`bill_decomposition_runs`). `taxonomyVersion` is legacy (the taxonomy approach, superseded by
 * vectors-primary; NULL going forward).
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
  decompositionSnapshotVersion: Option[Int],
  runId: Option[Long],
)

object BillConceptGroupDO {

  import com.repcheck.utils.doobie.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillConceptGroupDO] = deriveEncoder[BillConceptGroupDO]
  implicit val decoder: Decoder[BillConceptGroupDO] = deriveDecoder[BillConceptGroupDO]

}
