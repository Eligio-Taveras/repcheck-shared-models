package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * The manifest for one corpus snapshot (SNAP/D22): the system pre-LLM metadata state frozen at capture-time T, keyed by
 * the monotonic `snapshotVersion` (Int — snapshots are coarse, one per T, shared across all users). All decomposition
 * runs process OFF this frozen snapshot; outputs are tagged with `snapshotVersion` for reproducibility. `status` is one
 * of capturing | active | superseded.
 */
final case class PreLlmMetadataSnapshotDO(
  snapshotVersion: Int,
  createdAt: Option[Instant],
  status: String,
)

object PreLlmMetadataSnapshotDO {
  implicit val encoder: Encoder[PreLlmMetadataSnapshotDO] = deriveEncoder[PreLlmMetadataSnapshotDO]
  implicit val decoder: Decoder[PreLlmMetadataSnapshotDO] = deriveDecoder[PreLlmMetadataSnapshotDO]
}
