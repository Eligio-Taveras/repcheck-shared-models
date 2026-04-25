package repcheck.shared.models.congress.dos.bill

import java.time.{Instant, LocalDate}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.common.FormatType

/**
 * One row per Congress.gov bill text version (IH, IS, PL, …).
 *
 * As of db-migrations 026 this DO holds only version metadata: which version
 * exists for the bill, where its canonical URL points, when it was fetched.
 * The actual text and per-chunk embeddings live in
 * [[RawBillTextDO]] rows, joined by `version_id`. A version-row without any
 * `RawBillTextDO` children means the text hasn't been fetched + embedded yet.
 */
final case class BillTextVersionDO(
  id: Long,
  billId: Long,
  versionCode: String,
  versionType: String,
  versionDate: Option[LocalDate],
  formatType: Option[FormatType],
  url: Option[String],
  fetchedAt: Option[Instant],
  createdAt: Option[Instant],
)

object BillTextVersionDO {

  import repcheck.shared.models.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

  implicit val encoder: Encoder[BillTextVersionDO] = deriveEncoder[BillTextVersionDO]
  implicit val decoder: Decoder[BillTextVersionDO] = deriveDecoder[BillTextVersionDO]

}
