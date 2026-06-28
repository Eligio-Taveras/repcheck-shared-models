package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * Provenance for one decomposition sweep within a snapshot (SNAP/D22): the orchestrator/embedder/clusterer/prompt
 * versions used + lifecycle `status` (running | completed | completed_with_errors | failed). Concept groups produced by
 * the sweep link back via `runId`. `workflowRunId` is the optional votr `workflow_runs` id (launcher args(1)) for
 * execution-tracking traceability.
 */
final case class BillDecompositionRunDO(
  id: Long,
  snapshotVersion: Int,
  orchestratorVersion: String,
  embedderVersion: String,
  clustererVersion: String,
  promptVersion: String,
  status: String,
  startedAt: Option[Instant],
  completedAt: Option[Instant],
  workflowRunId: Option[Long],
)

object BillDecompositionRunDO {
  implicit val encoder: Encoder[BillDecompositionRunDO] = deriveEncoder[BillDecompositionRunDO]
  implicit val decoder: Decoder[BillDecompositionRunDO] = deriveDecoder[BillDecompositionRunDO]
}
