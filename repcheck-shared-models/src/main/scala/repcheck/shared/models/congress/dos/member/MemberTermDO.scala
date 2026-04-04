package repcheck.shared.models.congress.dos.member

import java.util.UUID

final case class MemberTermDO(
    termId: UUID,
    memberId: String,
    chamber: Option[String],
    congress: Option[Int],
    startYear: Option[Int],
    endYear: Option[Int],
    memberType: Option[String],
    stateCode: Option[String],
    stateName: Option[String],
    district: Option[Int]
)
