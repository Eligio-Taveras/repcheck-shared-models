package repcheck.shared.models.congress.dos.bill

import java.time.{Instant, LocalDate}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.bill.TextVersionCode
import repcheck.shared.models.congress.common.{BillType, Chamber, FormatType}
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
  textUrl: Option[String],
  textFormat: Option[FormatType],
  textVersionType: Option[TextVersionCode],
  textDate: Option[LocalDate],
  textContent: Option[String],
  textEmbedding: Option[Array[Float]],
  summaryText: Option[String],
  summaryActionDesc: Option[String],
  summaryActionDate: Option[LocalDate],
  updateDate: Option[Instant],
  updateDateIncludingText: Option[Instant],
  legislationUrl: Option[String],
  apiUrl: Option[String],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
  latestTextVersionId: Option[Long],
)

object BillDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}
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
        textUrl = None,
        textFormat = None,
        textVersionType = None,
        textDate = None,
        textContent = None,
        textEmbedding = None,
        summaryText = None,
        summaryActionDesc = None,
        summaryActionDate = None,
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
