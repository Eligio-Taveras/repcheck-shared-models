package repcheck.shared.models.congress.dos.amendment

import java.time.Instant

final case class AmendmentDO(
  amendmentId: String,
  congress: Int,
  amendmentType: Option[String],
  number: String,
  billId: Option[String],
  chamber: Option[String],
  description: Option[String],
  purpose: Option[String],
  sponsorBioguideId: Option[String],
  submittedDate: Option[String],
  latestActionDate: Option[String],
  latestActionText: Option[String],
  updateDate: Option[String],
  apiUrl: Option[String],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)
