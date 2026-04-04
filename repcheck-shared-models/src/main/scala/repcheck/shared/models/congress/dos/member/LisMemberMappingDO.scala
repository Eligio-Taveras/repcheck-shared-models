package repcheck.shared.models.congress.dos.member

import java.time.Instant

final case class LisMemberMappingDO(
    lisMemberId: String,
    memberId: String,
    lastVerified: Instant
)
