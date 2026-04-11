package repcheck.shared.models.congress.dto.conversions

import repcheck.shared.models.congress.amendment.AmendmentType
import repcheck.shared.models.congress.common.Chamber
import repcheck.shared.models.congress.dos.amendment.AmendmentDO
import repcheck.shared.models.congress.dto.amendment.AmendmentDetailDTO

object AmendmentConversions {

  private def parseOptChamber(raw: Option[String]): Either[String, Option[Chamber]] =
    raw match {
      case None    => Right(None)
      case Some(s) => Chamber.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  private def parseOptAmendmentType(raw: Option[String]): Either[String, Option[AmendmentType]] =
    raw match {
      case None    => Right(None)
      case Some(s) => AmendmentType.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  private[conversions] def buildAmendmentId(congress: Int, amendmentType: Option[String], number: String): String =
    s"$congress-${amendmentType.getOrElse("UNKNOWN")}-$number"

  private[conversions] def buildBillIdFromAmendedBill(
    congress: Option[Int],
    billType: Option[String],
    number: Option[Int],
  ): Option[String] =
    for {
      c  <- congress
      bt <- billType
      n  <- number
    } yield s"$c-${bt.toUpperCase}-$n"

  implicit class AmendmentDetailDTOOps(private val dto: AmendmentDetailDTO) extends AnyVal {

    def toDO: Either[String, AmendmentDO] =
      if (dto.congress <= 0) {
        Left(s"congress must be > 0, got: ${dto.congress}")
      } else if (dto.number.trim.isEmpty) {
        Left("number must not be empty")
      } else {
        val naturalKey = buildAmendmentId(dto.congress, dto.amendmentType, dto.number)

        for {
          amendmentType <- parseOptAmendmentType(dto.amendmentType)
          chamber       <- parseOptChamber(dto.chamber)
        } yield AmendmentDO(
          amendmentId = 0L,
          naturalKey = naturalKey,
          congress = dto.congress,
          amendmentType = amendmentType,
          number = dto.number,
          billId = None,
          chamber = chamber,
          description = dto.description,
          purpose = dto.purpose,
          sponsorMemberId = None,
          submittedDate = DateParsing.toLocalDate(dto.submittedDate),
          latestActionDate = DateParsing.toLocalDate(dto.latestAction.map(_.actionDate)),
          latestActionText = dto.latestAction.map(_.text),
          updateDate = DateParsing.toInstant(dto.updateDate),
          apiUrl = None,
          createdAt = None,
          updatedAt = None,
        )
      }

  }

}
