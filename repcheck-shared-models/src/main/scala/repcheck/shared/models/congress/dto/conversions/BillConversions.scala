package repcheck.shared.models.congress.dto.conversions

import repcheck.shared.models.congress.dos.bill.{BillCosponsorDO, BillDO, BillSubjectDO}
import repcheck.shared.models.congress.dos.results.BillConversionResult
import repcheck.shared.models.congress.dto.bill.{BillDetailDTO, BillListItemDTO}

object BillConversions {

  private[conversions] def buildBillId(congress: Int, billType: String, number: String): String =
    s"$congress-${billType.toUpperCase}-$number"

  private[conversions] def validateBillFields(
    congress: Int,
    number: String,
    title: String,
  ): Either[String, Unit] =
    if (congress <= 0) {
      Left(s"congress must be > 0, got: $congress")
    } else if (number.trim.isEmpty) {
      Left("number must not be empty")
    } else if (title.trim.isEmpty) {
      Left("title must not be empty")
    } else {
      Right(())
    }

  implicit class BillListItemDTOOps(private val dto: BillListItemDTO) extends AnyVal {

    def toDO: Either[String, BillDO] =
      for {
        _ <- validateBillFields(dto.congress, dto.number, dto.title)
      } yield BillDO(
        billId = buildBillId(dto.congress, dto.billType, dto.number),
        congress = dto.congress,
        billType = dto.billType,
        number = dto.number,
        title = dto.title,
        originChamber = dto.originChamber,
        originChamberCode = dto.originChamberCode,
        introducedDate = None,
        policyArea = None,
        latestActionDate = dto.latestAction.map(_.actionDate),
        latestActionText = dto.latestAction.map(_.text),
        constitutionalAuthorityText = None,
        sponsorBioguideId = None,
        textUrl = None,
        textFormat = None,
        textVersionType = None,
        textDate = None,
        textContent = None,
        textEmbedding = None,
        summaryText = None,
        summaryActionDesc = None,
        summaryActionDate = None,
        updateDate = dto.updateDate,
        updateDateIncludingText = dto.updateDateIncludingText,
        legislationUrl = None,
        apiUrl = Some(dto.url),
        createdAt = None,
        updatedAt = None,
      )

  }

  implicit class BillDetailDTOOps(private val dto: BillDetailDTO) extends AnyVal {

    def toDO: Either[String, BillConversionResult] =
      for {
        _ <- validateBillFields(dto.congress, dto.number, dto.title)
      } yield {
        val billId = buildBillId(dto.congress, dto.billType, dto.number)

        val sponsorBioguideId = dto.sponsors.flatMap(_.headOption).map(_.bioguideId)

        val firstTextVersion = dto.textVersions.flatMap(_.headOption)
        val firstFormat      = firstTextVersion.flatMap(_.formats).flatMap(_.headOption)
        val textUrl          = firstFormat.map(_.url)
        val textFormat       = firstFormat.map(_.type_)
        val textVersionType  = firstTextVersion.flatMap(_.type_)
        val textDate         = firstTextVersion.flatMap(_.date)

        val firstSummary      = dto.summaries.flatMap(_.headOption)
        val summaryText       = firstSummary.flatMap(_.text)
        val summaryActionDesc = firstSummary.flatMap(_.actionDesc)
        val summaryActionDate = firstSummary.flatMap(_.actionDate)

        val bill = BillDO(
          billId = billId,
          congress = dto.congress,
          billType = dto.billType,
          number = dto.number,
          title = dto.title,
          originChamber = dto.originChamber,
          originChamberCode = dto.originChamberCode,
          introducedDate = dto.introducedDate,
          policyArea = dto.policyArea,
          latestActionDate = dto.latestAction.map(_.actionDate),
          latestActionText = dto.latestAction.map(_.text),
          constitutionalAuthorityText = dto.constitutionalAuthorityStatementText,
          sponsorBioguideId = sponsorBioguideId,
          textUrl = textUrl,
          textFormat = textFormat,
          textVersionType = textVersionType,
          textDate = textDate,
          textContent = None,
          textEmbedding = None,
          summaryText = summaryText,
          summaryActionDesc = summaryActionDesc,
          summaryActionDate = summaryActionDate,
          updateDate = dto.updateDate,
          updateDateIncludingText = dto.updateDateIncludingText,
          legislationUrl = dto.legislationUrl,
          apiUrl = Some(dto.url),
          createdAt = None,
          updatedAt = None,
        )

        val cosponsors: List[BillCosponsorDO] = List.empty

        val subjects: List[BillSubjectDO] = dto.subjects
          .flatMap(_.legislativeSubjects)
          .getOrElse(List.empty)
          .map { subj =>
            BillSubjectDO(
              billId = billId,
              subjectName = subj.name,
              embedding = None,
              updateDate = subj.updateDate,
            )
          }

        BillConversionResult(
          bill = bill,
          cosponsors = cosponsors,
          subjects = subjects,
        )
      }

  }

}
