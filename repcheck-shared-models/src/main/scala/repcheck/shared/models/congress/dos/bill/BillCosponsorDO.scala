package repcheck.shared.models.congress.dos.bill

final case class BillCosponsorDO(
  billId: String,
  memberId: String,
  isOriginalCosponsor: Option[Boolean],
  sponsorshipDate: Option[String],
)
