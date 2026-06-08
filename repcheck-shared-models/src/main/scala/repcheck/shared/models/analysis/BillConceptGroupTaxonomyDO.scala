package repcheck.shared.models.analysis

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * A scored taxonomy assignment for a concept group (multi-label): one row per `(groupId, taxonomyNodeId)` with a
 * `score` in [0, 1]. A group has 1..N rows, or exactly one pointing at the reserved `unclassified` node. This is the
 * discrete cross-bill handle — `GROUP BY taxonomyNodeId` spans bills.
 */
final case class BillConceptGroupTaxonomyDO(groupId: Long, taxonomyNodeId: Long, score: Float)

object BillConceptGroupTaxonomyDO {
  implicit val encoder: Encoder[BillConceptGroupTaxonomyDO] = deriveEncoder[BillConceptGroupTaxonomyDO]
  implicit val decoder: Decoder[BillConceptGroupTaxonomyDO] = deriveDecoder[BillConceptGroupTaxonomyDO]
}
