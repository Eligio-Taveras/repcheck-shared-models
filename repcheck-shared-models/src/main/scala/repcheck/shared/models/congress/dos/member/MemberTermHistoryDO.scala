package repcheck.shared.models.congress.dos.member

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.common.{Chamber, UsState}
import repcheck.shared.models.congress.member.MemberType

final case class MemberTermHistoryDO(
  historyId: Long,
  memberId: Long,
  chamber: Option[Chamber],
  congress: Option[Int],
  startYear: Option[Int],
  endYear: Option[Int],
  memberType: Option[MemberType],
  stateCode: Option[UsState],
  stateName: Option[String],
  district: Option[Int],
  archivedAt: Option[Instant],
)

object MemberTermHistoryDO {

  implicit val encoder: Encoder[MemberTermHistoryDO] = deriveEncoder[MemberTermHistoryDO]
  implicit val decoder: Decoder[MemberTermHistoryDO] = deriveDecoder[MemberTermHistoryDO]

}
