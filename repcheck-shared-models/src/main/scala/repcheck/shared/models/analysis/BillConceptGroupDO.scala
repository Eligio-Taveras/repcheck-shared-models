package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * A concept group within a bill version: a cluster of sections about the same thing. `label` + `conceptSummary` are the
 * LLM-extracted concept; `embedding` is the embedding of that summary; `taxonomyVersion` records which taxonomy build
 * the group was classified under (NULL until classified — its scored node assignments live in
 * `bill_concept_group_taxonomy`).
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
)

object BillConceptGroupDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillConceptGroupDO] = deriveEncoder[BillConceptGroupDO]
  implicit val decoder: Decoder[BillConceptGroupDO] = deriveDecoder[BillConceptGroupDO]

}
