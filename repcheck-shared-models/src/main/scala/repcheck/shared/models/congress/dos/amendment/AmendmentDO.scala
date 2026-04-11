package repcheck.shared.models.congress.dos.amendment

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.amendment.AmendmentType
import repcheck.shared.models.congress.common.Chamber
import repcheck.shared.models.placeholder.HasPlaceholder

final case class AmendmentDO(
  amendmentId: Long,
  naturalKey: String,
  congress: Int,
  amendmentType: Option[AmendmentType],
  number: String,
  billId: Option[Long],
  chamber: Option[Chamber],
  description: Option[String],
  purpose: Option[String],
  sponsorMemberId: Option[Long],
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
        amendmentId = 0L,
        naturalKey = naturalKey,
        congress = 0,
        amendmentType = None,
        number = "",
        billId = None,
        chamber = None,
        description = None,
        purpose = None,
        sponsorMemberId = None,
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
