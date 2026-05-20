package repcheck.shared.models.congress.dos.amendment

import java.time.{Instant, LocalDate}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.amendment.{AmendmentType, SponsorType}
import repcheck.shared.models.congress.common.Chamber
import repcheck.shared.models.placeholder.HasPlaceholder

final case class AmendmentDO(
  amendmentId: Long,
  naturalKey: String,
  congress: Int,
  amendmentType: Option[AmendmentType],
  number: String,
  billId: Option[Long],
  chamber: Chamber,
  description: Option[String],
  purpose: Option[String],
  sponsorMemberId: Option[Long],
  sponsorCommitteeId: Option[Long],
  sponsorType: Option[SponsorType],
  submittedDate: Option[LocalDate],
  proposedDate: Option[LocalDate],
  latestActionDate: Option[LocalDate],
  latestActionTime: Option[String],
  latestActionText: Option[String],
  updateDate: Option[Instant],
  apiUrl: Option[String],
  parentAmendmentId: Option[Long],
  lastTextCheckAt: Option[Instant],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object AmendmentDO {

  import repcheck.shared.models.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

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
        // Per L9, chamber is NOT NULL in storage. Both ingestion paths (Congress.gov and Senate XML) deterministically
        // derive it from amendmentType. Placeholders only exist transiently as forward references and are immediately
        // overwritten by the owning pipeline's full upsert; House is a benign sentinel for that brief window — same
        // pattern BillDO uses with `BillType.HR`.
        chamber = Chamber.House,
        description = None,
        purpose = None,
        sponsorMemberId = None,
        sponsorCommitteeId = None,
        sponsorType = None,
        submittedDate = None,
        proposedDate = None,
        latestActionDate = None,
        latestActionTime = None,
        latestActionText = None,
        updateDate = None,
        apiUrl = None,
        parentAmendmentId = None,
        lastTextCheckAt = None,
        createdAt = None,
        updatedAt = None,
      )
  }

}
