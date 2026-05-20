package repcheck.shared.models.congress.dto.conversions

import repcheck.shared.models.congress.amendment.{AmendmentType, SponsorType}
import repcheck.shared.models.congress.common.Chamber
import repcheck.shared.models.congress.dos.amendment.AmendmentDO
import repcheck.shared.models.congress.dto.amendment.AmendmentDetailDTO
import repcheck.shared.models.congress.dto.bill.SponsorDTO

object AmendmentConversions {

  private def parseOptAmendmentType(raw: Option[String]): Either[String, Option[AmendmentType]] =
    raw match {
      case None    => Right(None)
      case Some(s) => AmendmentType.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  /**
   * Resolve the storage chamber for an amendment.
   *
   * Per L9, `amendments.chamber` is NOT NULL — the schema requires a value. Both ingestion paths produce one
   * deterministically:
   *   - Congress.gov detail responses include `chamber` ("House" | "Senate") for every amendment.
   *   - Senate XML lacks an explicit chamber but always carries `amendmentType`, which uniquely determines the chamber.
   *
   * Resolution order:
   *   1. Trust the explicit DTO `chamber` if present and parseable. 2. Fall back to deriving from `amendmentType`
   *      (HAMDT → House, SAMDT/SUAMDT → Senate). 3. If neither is available, return Left — at this point we can't
   *      satisfy the NOT NULL contract and the row should be rejected as malformed upstream data.
   */
  private def resolveChamber(
    rawChamber: Option[String],
    amendmentType: Option[AmendmentType],
  ): Either[String, Chamber] =
    rawChamber match {
      case Some(s) => Chamber.fromString(s).left.map(_.getMessage)
      case None =>
        amendmentType match {
          case Some(AmendmentType.HAMDT)                              => Right(Chamber.House)
          case Some(AmendmentType.SAMDT) | Some(AmendmentType.SUAMDT) => Right(Chamber.Senate)
          case None =>
            Left(
              "Cannot resolve chamber: DTO has neither an explicit chamber nor a recognizable amendmentType to derive it from"
            )
        }
    }

  private[conversions] def buildAmendmentId(congress: Int, amendmentType: Option[String], number: String): String =
    s"$congress-${amendmentType.getOrElse("UNKNOWN")}-$number"

  private[conversions] def buildBillIdFromAmendedBill(
    congress: Option[Int],
    billType: Option[String],
    number: Option[String],
  ): Option[String] =
    for {
      c  <- congress
      bt <- billType
      n  <- number
    } yield BillConversions.buildBillNaturalKey(c, bt, n)

  /** Derive sponsor type from the DTO's sponsor list shape when no resolved IDs are available. */
  private def deriveSponsorTypeFromDTO(sponsors: Option[List[SponsorDTO]]): Option[SponsorType] =
    sponsors.flatMap(_.headOption).map {
      case _: SponsorDTO.MemberSponsorDTO    => SponsorType.Member
      case _: SponsorDTO.CommitteeSponsorDTO => SponsorType.Committee
    }

  /**
   * Resolve `sponsorType` from the provided IDs, falling back to DTO derivation.
   *
   * Priority: resolved IDs take precedence (they reflect the actual DB state); DTO shape is the fallback for callers
   * that haven't resolved IDs yet. Both member and committee IDs set simultaneously is rejected — the schema enforces
   * mutual exclusivity.
   */
  private def resolveSponsorType(
    sponsorMemberId: Option[Long],
    sponsorCommitteeId: Option[Long],
    sponsors: Option[List[SponsorDTO]],
  ): Either[String, Option[SponsorType]] =
    (sponsorMemberId, sponsorCommitteeId) match {
      case (Some(_), Some(_)) => Left("sponsorMemberId and sponsorCommitteeId are mutually exclusive")
      case (Some(_), None)    => Right(Some(SponsorType.Member))
      case (None, Some(_))    => Right(Some(SponsorType.Committee))
      case (None, None)       => Right(deriveSponsorTypeFromDTO(sponsors))
    }

  implicit class AmendmentDetailDTOOps(private val dto: AmendmentDetailDTO) extends AnyVal {

    /**
     * Build an `AmendmentDO`, optionally substituting caller-resolved surrogate ids.
     *
     * `sponsorType` is computed automatically: from the provided sponsor IDs when present (member ID → Member,
     * committee ID → Committee), falling back to the DTO's sponsor list shape. Passing both `sponsorMemberId` and
     * `sponsorCommitteeId` is rejected. Callers that don't need resolved cross-entity ids can call `dto.toDO()`.
     */
    def toDO(
      billId: Option[Long] = None,
      sponsorMemberId: Option[Long] = None,
      sponsorCommitteeId: Option[Long] = None,
      parentAmendmentId: Option[Long] = None,
    ): Either[String, AmendmentDO] =
      if (dto.congress <= 0) {
        Left(s"congress must be > 0, got: ${dto.congress}")
      } else if (dto.number.trim.isEmpty) {
        Left("number must not be empty")
      } else {
        val naturalKey = buildAmendmentId(dto.congress, dto.amendmentType, dto.number)

        for {
          amendmentType <- parseOptAmendmentType(dto.amendmentType)
          chamber       <- resolveChamber(dto.chamber, amendmentType)
          sponsorType   <- resolveSponsorType(sponsorMemberId, sponsorCommitteeId, dto.sponsors)
        } yield AmendmentDO(
          amendmentId = 0L,
          naturalKey = naturalKey,
          congress = dto.congress,
          amendmentType = amendmentType,
          number = dto.number,
          billId = billId,
          chamber = chamber,
          description = dto.description,
          purpose = dto.purpose,
          sponsorMemberId = sponsorMemberId,
          sponsorCommitteeId = sponsorCommitteeId,
          sponsorType = sponsorType,
          submittedDate = DateParsing.toLocalDate(dto.submittedDate),
          proposedDate = DateParsing.toLocalDate(dto.proposedDate),
          latestActionDate = DateParsing.toLocalDate(dto.latestAction.map(_.actionDate)),
          latestActionTime = dto.latestAction.flatMap(_.actionTime),
          latestActionText = dto.latestAction.map(_.text),
          updateDate = DateParsing.toInstant(dto.updateDate),
          apiUrl = None,
          parentAmendmentId = parentAmendmentId,
          lastTextCheckAt = None,
          createdAt = None,
          updatedAt = None,
        )
      }

  }

}
