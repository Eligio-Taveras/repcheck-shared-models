package repcheck.shared.models.congress.dos.errors

final case class BillConversionFailed(message: String)
    extends Exception(s"Bill conversion failed: $message")

final case class MemberConversionFailed(message: String)
    extends Exception(s"Member conversion failed: $message")

final case class VoteConversionFailed(message: String)
    extends Exception(s"Vote conversion failed: $message")

final case class AmendmentConversionFailed(message: String)
    extends Exception(s"Amendment conversion failed: $message")
