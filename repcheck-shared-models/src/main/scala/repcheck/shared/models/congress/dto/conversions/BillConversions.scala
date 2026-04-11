package repcheck.shared.models.congress.dto.conversions

import repcheck.shared.models.congress.bill.TextVersionCode
import repcheck.shared.models.congress.common.{BillType, Chamber, FormatType}
import repcheck.shared.models.congress.dos.bill.{BillCosponsorDO, BillDO, BillSubjectDO}
import repcheck.shared.models.congress.dos.results.BillConversionResult
import repcheck.shared.models.congress.dto.bill.{BillDetailDTO, BillListItemDTO}

object BillConversions {

  private def parseChamber(raw: Option[String]): Either[String, Option[Chamber]] =
    raw match {
      case None    => Right(None)
      case Some(s) => Chamber.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  private def parseFormatType(raw: Option[String]): Either[String, Option[FormatType]] =
    raw match {
      case None    => Right(None)
      case Some(s) => FormatType.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  private def parseTextVersionCode(raw: Option[String]): Either[String, Option[TextVersionCode]] =
    raw match {
      case None    => Right(None)
      case Some(s) => TextVersionCode.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  private def parseBillType(raw: String): Either[String, BillType] =
    BillType.fromString(raw).left.map(_.getMessage)

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
        _             <- validateBillFields(dto.congress, dto.number, dto.title)
        billType      <- parseBillType(dto.billType)
        originChamber <- parseChamber(dto.originChamber)
      } yield BillDO(
        billId = 0L,
        naturalKey = buildBillId(dto.congress, dto.billType, dto.number),
        congress = dto.congress,
        billType = billType,
        number = dto.number,
        title = dto.title,
        originChamber = originChamber,
        originChamberCode = dto.originChamberCode,
        introducedDate = None,
        policyArea = None,
        latestActionDate = DateParsing.toLocalDate(dto.latestAction.map(_.actionDate)),
        latestActionText = dto.latestAction.map(_.text),
        constitutionalAuthorityText = None,
        sponsorMemberId = None,
        textUrl = None,
        textFormat = None,
        textVersionType = None,
        textDate = None,
        textContent = None,
        textEmbedding = None,
        summaryText = None,
        summaryActionDesc = None,
        summaryActionDate = None,
        updateDate = DateParsing.toInstant(dto.updateDate),
        updateDateIncludingText = DateParsing.toInstant(dto.updateDateIncludingText),
        legislationUrl = None,
        apiUrl = Some(dto.url),
        createdAt = None,
        updatedAt = None,
        latestTextVersionId = None,
      )

  }

  implicit class BillDetailDTOOps(private val dto: BillDetailDTO) extends AnyVal {

    def toDO: Either[String, BillConversionResult] = {
      val firstTextVersion = dto.textVersions.flatMap(_.headOption)
      val firstFormat      = firstTextVersion.flatMap(_.formats).flatMap(_.headOption)

      for {
        _               <- validateBillFields(dto.congress, dto.number, dto.title)
        billType        <- parseBillType(dto.billType)
        originChamber   <- parseChamber(dto.originChamber)
        textFormatVal   <- parseFormatType(firstFormat.map(_.type_))
        textVersionType <- parseTextVersionCode(firstTextVersion.flatMap(_.type_))
      } yield {
        val naturalKey = buildBillId(dto.congress, dto.billType, dto.number)

        val textUrl  = firstFormat.map(_.url)
        val textDate = firstTextVersion.flatMap(_.date)

        val firstSummary      = dto.summaries.flatMap(_.headOption)
        val summaryText       = firstSummary.flatMap(_.text)
        val summaryActionDesc = firstSummary.flatMap(_.actionDesc)
        val summaryActionDate = firstSummary.flatMap(_.actionDate)

        val bill = BillDO(
          billId = 0L,
          naturalKey = naturalKey,
          congress = dto.congress,
          billType = billType,
          number = dto.number,
          title = dto.title,
          originChamber = originChamber,
          originChamberCode = dto.originChamberCode,
          introducedDate = DateParsing.toLocalDate(dto.introducedDate),
          policyArea = dto.policyArea,
          latestActionDate = DateParsing.toLocalDate(dto.latestAction.map(_.actionDate)),
          latestActionText = dto.latestAction.map(_.text),
          constitutionalAuthorityText = dto.constitutionalAuthorityStatementText,
          sponsorMemberId = None,
          textUrl = textUrl,
          textFormat = textFormatVal,
          textVersionType = textVersionType,
          textDate = DateParsing.toLocalDate(textDate),
          textContent = None,
          textEmbedding = None,
          summaryText = summaryText,
          summaryActionDesc = summaryActionDesc,
          summaryActionDate = DateParsing.toLocalDate(summaryActionDate),
          updateDate = DateParsing.toInstant(dto.updateDate),
          updateDateIncludingText = DateParsing.toInstant(dto.updateDateIncludingText),
          legislationUrl = dto.legislationUrl,
          apiUrl = Some(dto.url),
          createdAt = None,
          updatedAt = None,
          latestTextVersionId = None,
        )

        // Cosponsors are fetched separately via paginated cosponsor endpoint;
        // BillDetailDTO only carries a PaginationInfoDTO with count/url.
        // Pipeline populates cosponsors in a dedicated step after toDO conversion.
        val cosponsors: List[BillCosponsorDO] = List.empty

        val subjects: List[BillSubjectDO] = dto.subjects
          .flatMap(_.legislativeSubjects)
          .getOrElse(List.empty)
          .map { subj =>
            BillSubjectDO(
              billId = 0L,
              subjectName = subj.name,
              embedding = None,
              updateDate = DateParsing.toInstant(subj.updateDate),
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

}
