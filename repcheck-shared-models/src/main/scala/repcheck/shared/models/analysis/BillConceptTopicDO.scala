package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.llm.{Effect, Impact, Scope}

/**
 * A stance-tagged topic of a concept group (vectors-primary). `topic` is the neutrally-framed noun phrase whose
 * `topicEmbedding` is the cross-bill retrieval vector; `phrase` + `effect`/`impact`/`scope`/`entity` are stance
 * metadata for alignment scoring (Component 11). `conceptGroupId` is a FK to `bill_concept_groups` (ON DELETE CASCADE).
 * Doobie `Read`/`Write` are derived at the persister (with the enum `Meta` + pgvector codecs there).
 */
final case class BillConceptTopicDO(
  id: Long,
  conceptGroupId: Long,
  phrase: String,
  topic: String,
  effect: Effect,
  entity: String,
  impact: Impact,
  scope: Scope,
  topicEmbedding: Option[Array[Float]],
  createdAt: Option[Instant],
)

object BillConceptTopicDO {

  import com.repcheck.utils.doobie.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillConceptTopicDO] = deriveEncoder[BillConceptTopicDO]
  implicit val decoder: Decoder[BillConceptTopicDO] = deriveDecoder[BillConceptTopicDO]

}
