package repcheck.shared.models.congress.dos.bill

import java.time.{Instant, LocalDate}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * One row per CRS bill summary stage (Introduced, Reported, Engrossed, Passed, Public Law, …).
 *
 * CRS republishes a bill's summary at each major legislative stage. `bill_summaries` (db-migrations 049) keeps one row
 * per stage with a FK back to `bills`, mirroring [[BillTextVersionDO]] — replacing the single `bills.summary_text`
 * column, which could hold only one stage and was being clobbered to NULL by the bill-metadata upsert. Owned by
 * bill-summary-pipeline; the unique key is `(bill_id, version_code)`.
 */
final case class BillSummaryDO(
  id: Long,
  billId: Long,
  versionCode: String,
  actionDate: Option[LocalDate],
  actionDesc: Option[String],
  text: String,
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object BillSummaryDO {

  import com.repcheck.utils.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

  implicit val encoder: Encoder[BillSummaryDO] = deriveEncoder[BillSummaryDO]
  implicit val decoder: Decoder[BillSummaryDO] = deriveDecoder[BillSummaryDO]

}
