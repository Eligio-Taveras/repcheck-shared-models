package repcheck.shared.models.congress.dto.conversions

import repcheck.shared.models.congress.dos.amendment.AmendmentDO
import repcheck.shared.models.congress.dto.amendment.AmendmentDetailDTO

object AmendmentConversions {

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

        Right(
          AmendmentDO(
            amendmentId = 0L,
            naturalKey = naturalKey,
            congress = dto.congress,
            amendmentType = dto.amendmentType,
            number = dto.number,
            billId = None,
            chamber = dto.chamber,
            description = dto.description,
            purpose = dto.purpose,
            sponsorMemberId = None,
            submittedDate = dto.submittedDate,
            latestActionDate = dto.latestAction.map(_.actionDate),
            latestActionText = dto.latestAction.map(_.text),
            updateDate = dto.updateDate,
            apiUrl = None,
            createdAt = None,
            updatedAt = None,
          )
        )
      }

  }

}
