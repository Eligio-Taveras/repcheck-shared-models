package repcheck.shared.models.congress.dos.bill

import java.time.Instant

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
)
