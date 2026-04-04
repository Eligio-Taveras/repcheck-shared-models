package repcheck.shared.models.congress.dos.bill

final case class BillSubjectDO(
  billId: String,
  subjectName: String,
  embedding: Option[Array[Float]],
  updateDate: Option[String],
)
