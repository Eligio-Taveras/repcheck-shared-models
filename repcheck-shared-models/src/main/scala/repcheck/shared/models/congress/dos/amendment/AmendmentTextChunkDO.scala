package repcheck.shared.models.congress.dos.amendment

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * One chunk of an amendment's extracted plain text (db-migrations 040).
 *
 * Mirrors [[repcheck.shared.models.congress.dos.bill.RawBillTextDO]] for the amendment side. The §7.6
 * amendment-text-pipeline splits each downloaded version's text into chunks sized to fit the embedding model input
 * (configured via `OLLAMA_MAX_CHUNK_CHARS`, default 12000), embeds each independently with qwen3-embedding:0.6b
 * (1024-dim output), and writes one row per chunk. `chunkIndex` is zero-based; ordering by it reconstructs the full
 * document.
 *
 * `versionId` is `Option[Long]` to mirror the nullable FK in the schema — chunks can briefly exist before being linked
 * to a parent `amendment_text_versions` row, although in steady state the pipeline always sets it.
 *
 * `embedding` is `Option[Array[Float]]` so the row is insertable atomically even when the embedding call hasn't yet
 * completed. Vector search ignores rows where it is still NULL.
 */
final case class AmendmentTextChunkDO(
  id: Long,
  amendmentId: Long,
  versionId: Option[Long],
  chunkIndex: Int,
  content: String,
  embedding: Option[Array[Float]],
  createdAt: Option[Instant],
)

object AmendmentTextChunkDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[AmendmentTextChunkDO] = deriveEncoder[AmendmentTextChunkDO]
  implicit val decoder: Decoder[AmendmentTextChunkDO] = deriveDecoder[AmendmentTextChunkDO]

}
