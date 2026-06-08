package repcheck.shared.models.analysis

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * A node in OUR LLM-built taxonomy (`concept_taxonomy`): `name` + `description`, `parentId` for the hierarchy,
 * `embedding` of (name + description) for retrieval/scoring, the `version` it belongs to, and its `status` (`proposed`
 * until a controlled refresh promotes it to `active`).
 */
final case class TaxonomyNodeDO(
  id: Long,
  name: String,
  parentId: Option[Long],
  description: String,
  embedding: Option[Array[Float]],
  version: Int,
  status: String,
)

object TaxonomyNodeDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[TaxonomyNodeDO] = deriveEncoder[TaxonomyNodeDO]
  implicit val decoder: Decoder[TaxonomyNodeDO] = deriveDecoder[TaxonomyNodeDO]

}
