package repcheck.shared.models.congress.dos.member

import java.time.Instant

final case class MemberDO(
  memberId: String,
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
