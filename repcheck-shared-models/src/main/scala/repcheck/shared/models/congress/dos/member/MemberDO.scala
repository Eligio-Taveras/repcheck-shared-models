package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.placeholder.HasPlaceholder

final case class MemberDO(
  memberId: Long,
  naturalKey: String,
  firstName: Option[String],
  lastName: Option[String],
  directOrderName: Option[String],
  invertedOrderName: Option[String],
  honorificName: Option[String],
  birthYear: Option[String],
  currentParty: Option[String],
  state: Option[String],
  district: Option[Int],
  imageUrl: Option[String],
  imageAttribution: Option[String],
  officialUrl: Option[String],
  updateDate: Option[String],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object MemberDO {

  implicit val encoder: Encoder[MemberDO] = deriveEncoder[MemberDO]
  implicit val decoder: Decoder[MemberDO] = deriveDecoder[MemberDO]

  implicit val hasPlaceholder: HasPlaceholder[MemberDO] = new HasPlaceholder[MemberDO] {
    def placeholder(naturalKey: String): MemberDO =
      MemberDO(
        memberId = 0L,
        naturalKey = naturalKey,
        firstName = None,
        lastName = None,
        directOrderName = None,
        invertedOrderName = None,
        honorificName = None,
        birthYear = None,
        currentParty = None,
        state = None,
        district = None,
        imageUrl = None,
        imageAttribution = None,
        officialUrl = None,
        updateDate = None,
        createdAt = None,
        updatedAt = None,
      )
  }

}
