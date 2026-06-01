package repcheck.shared.models.congress.dos.bill

import java.time.{Instant, LocalDate}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.bill.TextVersionCode
import repcheck.shared.models.congress.common.{BillType, Chamber}
import repcheck.shared.models.placeholder.HasPlaceholder

final case class BillDO(
  billId: Long,
  naturalKey: String,
  congress: Int,
  billType: BillType,
  number: String,
  title: String,
  originChamber: Option[Chamber],
  originChamberCode: Option[String],
  introducedDate: Option[LocalDate],
  policyArea: Option[String],
  latestActionDate: Option[LocalDate],
  latestActionText: Option[String],
  constitutionalAuthorityText: Option[String],
  sponsorMemberId: Option[Long],
  // textVersionType is the bill's current text stage. Read from bill_text_versions (via
  // bills.latest_text_version_id), not the retired bills.text_version_type column. The other
  // text_*/summary_* columns moved to bill_text_versions / bill_summaries and were dropped.
  textVersionType: Option[TextVersionCode],
  updateDate: Option[Instant],
  updateDateIncludingText: Option[Instant],
  legislationUrl: Option[String],
  apiUrl: Option[String],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
  latestTextVersionId: Option[Long],
)

object BillDO {

  import repcheck.shared.models.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

  implicit val encoder: Encoder[BillDO] = deriveEncoder[BillDO]
  implicit val decoder: Decoder[BillDO] = deriveDecoder[BillDO]

  implicit val hasPlaceholder: HasPlaceholder[BillDO] = new HasPlaceholder[BillDO] {
    def placeholder(naturalKey: String): BillDO =
      BillDO(
        billId = 0L,
        naturalKey = naturalKey,
        congress = 0,
        billType = BillType.HR,
        number = "",
        title = "",
        originChamber = None,
        originChamberCode = None,
        introducedDate = None,
        policyArea = None,
        latestActionDate = None,
        latestActionText = None,
        constitutionalAuthorityText = None,
        sponsorMemberId = None,
        textVersionType = None,
        updateDate = None,
        updateDateIncludingText = None,
        legislationUrl = None,
        apiUrl = None,
        createdAt = None,
        updatedAt = None,
        latestTextVersionId = None,
      )
  }

}
