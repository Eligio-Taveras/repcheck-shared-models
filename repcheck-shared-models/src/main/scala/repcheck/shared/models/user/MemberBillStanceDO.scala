package repcheck.shared.models.user

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class MemberBillStanceDO(
  memberId: String,
  billId: String,
  voteId: Option[String],
  position: Option[String],
  voteType: Option[String],
  voteDate: Option[String],
  congress: Option[Int],
  topics: List[String],
)

object MemberBillStanceDO {

  implicit val encoder: Encoder[MemberBillStanceDO] = deriveEncoder[MemberBillStanceDO]
  implicit val decoder: Decoder[MemberBillStanceDO] = deriveDecoder[MemberBillStanceDO]

}
