package repcheck.shared.models.analysis

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * One frozen member of a corpus snapshot (SNAP/D22): a `bill_text_versions` `versionId` captured into the snapshot,
 * with its `subjectCount` at capture time. No FK on `versionId` — version rows are immutable, so the manifest needs no
 * referential copy. Composite key (snapshotVersion, versionId).
 */
final case class PreLlmMetadataSnapshotMemberDO(
  snapshotVersion: Int,
  versionId: Long,
  subjectCount: Int,
)

object PreLlmMetadataSnapshotMemberDO {
  implicit val encoder: Encoder[PreLlmMetadataSnapshotMemberDO] = deriveEncoder[PreLlmMetadataSnapshotMemberDO]
  implicit val decoder: Decoder[PreLlmMetadataSnapshotMemberDO] = deriveDecoder[PreLlmMetadataSnapshotMemberDO]
}
