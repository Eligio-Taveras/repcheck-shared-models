package repcheck.shared.models.congress.dos.amendment

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.common.FormatType

/**
 * One row per amendment text version published by Congress.gov (Submitted / Modified, in HTML or PDF).
 *
 * Mirrors the bill-side [[repcheck.shared.models.congress.dos.bill.BillTextVersionDO]] but keyed on `amendments.id`
 * (BIGINT surrogate, not natural key — the live schema has been migrated).
 *
 * `versionType` carries the `amendment_text_version_code_type` enum value the writer persists. The §7.5 selector emits
 * the short codes `"SUB"` / `"MOD"` on the wire while the database enum (created by db-migrations 037) spells these out
 * as `"Submitted"` / `"Modified"`. Pipelines doing Doobie inserts must perform that mapping at write time; this DO
 * carries whatever string the row actually holds. Modeling it as `String` (not the enum case class) keeps this DO
 * independent of the enum spelling decision and reuses the existing [[repcheck.shared.models.codecs]] machinery for
 * plain-text columns.
 *
 * `formatType` reuses the bill-side [[FormatType]] enum because the live `format_type` column is the shared
 * `format_type_enum` (per migrations 007 + 037). Amendment text formats observed upstream are a strict subset of the
 * bill values: `"HTML"` (mapped to [[FormatType.FormattedText]]) or `"PDF"` ([[FormatType.PDF]]). No `"Formatted XML"`
 * granules are emitted for amendments.
 *
 * `downloadUrl` captures the rewritten api.govinfo.gov URL used by the §7.6 downloader, distinct from the
 * Congress.gov-source `url`. Storing both lets us audit which path was used per row and detect rewriter regressions if
 * patterns shift upstream.
 *
 * The legacy `content` and `embedding` columns (introduced in migration 007 before the chunked-text refactor) are
 * intentionally NOT modeled here — text + per-chunk embeddings live in [[AmendmentTextChunkDO]] rows joined by
 * `versionId`.
 */
final case class AmendmentTextVersionDO(
  id: Long,
  amendmentId: Long,
  versionType: String,
  versionDate: Instant,
  formatType: FormatType,
  url: String,
  downloadUrl: Option[String],
  textLength: Option[Int],
  fetchedAt: Option[Instant],
  createdAt: Option[Instant],
)

object AmendmentTextVersionDO {

  implicit val encoder: Encoder[AmendmentTextVersionDO] = deriveEncoder[AmendmentTextVersionDO]
  implicit val decoder: Decoder[AmendmentTextVersionDO] = deriveDecoder[AmendmentTextVersionDO]

}
