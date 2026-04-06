package repcheck.shared.models.congress.dos.bill

import java.time.Instant
import java.util.UUID

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.placeholder.HasPlaceholder

final case class BillDO(
  billId: String,
  congress: Int,
  billType: String,
  number: String,
  title: String,
  originChamber: Option[String],
  originChamberCode: Option[String],
  introducedDate: Option[String],
  policyArea: Option[String],
  latestActionDate: Option[String],
  latestActionText: Option[String],
  constitutionalAuthorityText: Option[String],
  sponsorBioguideId: Option[String],
  textUrl: Option[String],
  textFormat: Option[String],
  textVersionType: Option[String],
  textDate: Option[String],
  textContent: Option[String],
  textEmbedding: Option[Array[Float]],
  summaryText: Option[String],
  summaryActionDesc: Option[String],
  summaryActionDate: Option[String],
  updateDate: Option[String],
  updateDateIncludingText: Option[String],
  legislationUrl: Option[String],
  apiUrl: Option[String],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
  latestTextVersionId: Option[UUID],
)

object BillDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillDO] = deriveEncoder[BillDO]
  implicit val decoder: Decoder[BillDO] = deriveDecoder[BillDO]

  implicit val hasPlaceholder: HasPlaceholder[BillDO] = new HasPlaceholder[BillDO] {
    def placeholder(naturalKey: String): BillDO =
      BillDO(
        billId = naturalKey,
        congress = 0,
        billType = "",
        number = "",
        title = "",
        originChamber = None,
        originChamberCode = None,
        introducedDate = None,
        policyArea = None,
        latestActionDate = None,
        latestActionText = None,
        constitutionalAuthorityText = None,
        sponsorBioguideId = None,
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
