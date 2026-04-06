package repcheck.shared.models.congress.dos.amendment

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.placeholder.HasPlaceholder

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

object AmendmentDO {

  implicit val encoder: Encoder[AmendmentDO] = deriveEncoder[AmendmentDO]
  implicit val decoder: Decoder[AmendmentDO] = deriveDecoder[AmendmentDO]

  implicit val hasPlaceholder: HasPlaceholder[AmendmentDO] = new HasPlaceholder[AmendmentDO] {
    def placeholder(naturalKey: String): AmendmentDO =
      AmendmentDO(
        amendmentId = naturalKey,
        congress = 0,
        amendmentType = None,
        number = "",
        billId = None,
        chamber = None,
        description = None,
        purpose = None,
        sponsorBioguideId = None,
        submittedDate = None,
        latestActionDate = None,
        latestActionText = None,
        updateDate = None,
        apiUrl = None,
        createdAt = None,
        updatedAt = None,
      )
  }

}
