package repcheck.shared.models.congress.dos.bill

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * One chunk of a bill's raw text (db-migrations 026).
 *
 * The bill-text pipeline splits each version's full content into chunks sized to fit the embedding model input limit
 * (configured via `OLLAMA_MAX_CHUNK_CHARS`), embeds each chunk independently, and writes one `RawBillTextDO` per chunk.
 * `chunk_index` is the zero-based ordering within a parent version; `ORDER BY chunk_index` reconstructs the full
 * document.
 *
 * `versionId` is `Option[Long]` to match the DB's nullable FK — chunks can be attached to a bill even before a
 * `bill_text_versions` row is linked (for example if the availability checker hasn't associated a URL yet). In steady
 * state the pipeline always sets it.
 *
 * `embedding` is `Option[Array[Float]]` so a row can be inserted before the embedding call completes; the pipeline
 * fills it in-place once Ollama responds. Vector search skips rows where it is still NULL.
 */
final case class RawBillTextDO(
  id: Long,
  billId: Long,
  versionId: Option[Long],
  chunkIndex: Int,
  content: String,
  embedding: Option[Array[Float]],
  createdAt: Option[Instant],
)

object RawBillTextDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[RawBillTextDO] = deriveEncoder[RawBillTextDO]
  implicit val decoder: Decoder[RawBillTextDO] = deriveDecoder[RawBillTextDO]

}
