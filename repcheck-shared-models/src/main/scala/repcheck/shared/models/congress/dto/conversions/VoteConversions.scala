package repcheck.shared.models.congress.dto.conversions

import cats.Applicative
import cats.syntax.functor._
import cats.syntax.traverse._

import repcheck.shared.models.congress.common.{BillType, Chamber, Party, UsState}
import repcheck.shared.models.congress.dos.results.{UnresolvedVotePosition, VoteConversionResult}
import repcheck.shared.models.congress.dos.vote.VoteDO
import repcheck.shared.models.congress.dto.vote.{SenateVoteXmlDTO, VoteMembersDTO, VoteResultDTO}
import repcheck.shared.models.congress.vote.{VoteCast, VoteMethod, VoteType}

object VoteConversions {

  /**
   * Canonical natural-key builder for a roll-call vote. Used by every writer and every ON CONFLICT clause. Format:
   * `"$congress-$chamber-$session-$rollCallNumber"`, e.g., `"119-House-1-17"`. Chamber stays in its API casing
   * ("House"/"Senate"). Session is part of the key because Senate roll-call numbers reset per session within a
   * Congress. Always call this — do not inline the format string.
   */
  def buildVoteNaturalKey(congress: Int, chamber: String, session: Int, rollCallNumber: Int): String =
    s"$congress-$chamber-$session-$rollCallNumber"

  private def parseChamber(raw: String): Either[String, Chamber] =
    Chamber.fromString(raw).left.map(_.getMessage)

  private def parseLegislationType(raw: Option[String]): Either[String, Option[BillType]] =
    raw match {
      case None    => Right(None)
      case Some(s) => BillType.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  private def parseVoteCast(raw: Option[String]): Either[String, Option[VoteCast]] =
    raw match {
      case None    => Right(None)
      case Some(s) => VoteCast.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  private def parseParty(raw: Option[String]): Either[String, Option[Party]] =
    raw match {
      case None    => Right(None)
      case Some(s) => Party.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  private def parseState(raw: Option[String]): Either[String, Option[UsState]] =
    raw match {
      case None    => Right(None)
      case Some(s) => UsState.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  private def parseVoteMethod(raw: Option[String]): Either[String, Option[VoteMethod]] =
    raw match {
      case None    => Right(None)
      case Some(s) => VoteMethod.fromString(s).left.map(_.getMessage).map(Some(_))
    }

  /**
   * Construct the bill natural key when the DTO has the required legislation fields. Returns `None` for procedural
   * votes (no bill linkage). Used by the processor in Phase 2 to look up (or placeholder-create) the bill and set
   * [[VoteDO.billId]]. Delegates to [[BillConversions.buildBillNaturalKey]] to avoid format drift.
   */
  private def buildOptionalBillNaturalKey(
    congress: Int,
    legislationType: Option[String],
    legislationNumber: Option[String],
  ): Option[String] =
    for {
      t <- legislationType
      n <- legislationNumber
    } yield BillConversions.buildBillNaturalKey(congress, t, n)

  implicit class VoteMembersDTOOps(private val dto: VoteMembersDTO) extends AnyVal {

    /**
     * Convert this DTO into a [[VoteConversionResult]] with `billId` resolved.
     *
     * The conversion has two layers:
     *
     *   - **Pure validation** — congress > 0, chamber non-empty, sessionNumber present, and every enum-typed field
     *     parses (chamber, BillType, VoteCast, Party, UsState, VoteMethod). All emitted as `Left(reason)` if any check
     *     fails. Pure — no `F[_]` work happens until validation succeeds.
     *   - **Effectful billId resolution** — once validation passes, the `billLookup` function is invoked exactly once
     *     iff `billNaturalKey` is `Some`. For procedural votes (no legislation in the DTO), `billLookup` is never
     *     called and `vote.billId` is `None`.
     *
     * The caller (typically the votes-pipeline processor) supplies `billLookup` from the bills repository — usually a
     * wrapper that does `findIdByNaturalKey` followed by placeholder creation if missing, so the lookup always returns
     * `Some(id)` for bill-linked votes. If the lookup returns `None` for a bill-linked vote, `vote.billId` stays `None`
     * and the caller decides how to handle the unresolved bill.
     *
     * @param billLookup
     *   Resolves a bill's natural key (e.g., "118-HR-1234") to its DB Long PK. Called at most once per vote and only
     *   when `billNaturalKey` is `Some`.
     */
    def toDO[F[_]](
      billLookup: String => F[Option[Long]]
    )(using F: Applicative[F]): F[Either[String, VoteConversionResult]] =
      if (dto.congress <= 0) {
        F.pure(Left(s"congress must be > 0, got: ${dto.congress}"))
      } else if (dto.chamber.trim.isEmpty) {
        F.pure(Left("chamber must not be empty"))
      } else {
        dto.sessionNumber match {
          case None =>
            F.pure(Left("sessionNumber is required for vote natural-key construction"))
          case Some(session) =>
            val pureValidation: Either[String, (Option[Long] => VoteDO, Option[String], List[UnresolvedVotePosition])] =
              for {
                chamber         <- parseChamber(dto.chamber)
                legislationType <- parseLegislationType(dto.legislationType)
                voteMethod      <- parseVoteMethod(dto.voteType)
                positions <- dto.results
                  .getOrElse(List.empty)
                  .filter(_.memberId.isDefined)
                  .traverse { r =>
                    for {
                      position    <- parseVoteCast(r.voteCast)
                      partyAtVote <- parseParty(r.party)
                      stateAtVote <- parseState(r.state)
                    } yield UnresolvedVotePosition(
                      // Senate pre-resolves lis_member_id → bioguide in SenateVoteXmlDTO.toDO,
                      // so by the time we get here every populated memberId is a bioguide (Left).
                      // The Right variant is reserved for a future refactor (P2.3) where the
                      // Senate path passes unresolved lisMemberIds through to the processor.
                      memberSource = Left(r.memberId.getOrElse("")),
                      voteCast = position,
                      partyAtVote = partyAtVote,
                      stateAtVote = stateAtVote,
                    )
                  }
              } yield {
                val naturalKey     = buildVoteNaturalKey(dto.congress, dto.chamber, session, dto.rollCallNumber)
                val classifiedType = dto.voteQuestion.map(VoteType.fromQuestion)
                val billNaturalKey =
                  buildOptionalBillNaturalKey(dto.congress, dto.legislationType, dto.legislationNumber)

                val buildVoteDO: Option[Long] => VoteDO = resolvedBillId =>
                  VoteDO(
                    voteId = 0L,
                    naturalKey = naturalKey,
                    congress = dto.congress,
                    chamber = chamber,
                    rollNumber = dto.rollCallNumber,
                    sessionNumber = dto.sessionNumber,
                    billId = resolvedBillId,
                    question = dto.voteQuestion,
                    voteType = classifiedType,
                    voteMethod = voteMethod,
                    result = dto.result,
                    voteDate = DateParsing.toLocalDate(dto.startDate),
                    legislationNumber = dto.legislationNumber,
                    legislationType = legislationType,
                    legislationUrl = dto.legislationUrl,
                    sourceDataUrl = dto.sourceDataUrl.orElse(dto.url),
                    updateDate = DateParsing.toInstant(dto.updateDate),
                    createdAt = None,
                    updatedAt = None,
                  )

                (buildVoteDO, billNaturalKey, positions)
              }

            pureValidation match {
              case Left(err) =>
                F.pure(Left(err))
              case Right((buildVoteDO, billNaturalKey, positions)) =>
                val resolvedBillIdF: F[Option[Long]] = billNaturalKey match {
                  case None     => F.pure(None)
                  case Some(nk) => billLookup(nk)
                }
                resolvedBillIdF.map { billId =>
                  Right(
                    VoteConversionResult(
                      vote = buildVoteDO(billId),
                      billNaturalKey = billNaturalKey,
                      positions = positions,
                    )
                  )
                }
            }
        }
      }

  }

  implicit class SenateVoteXmlDTOOps(private val dto: SenateVoteXmlDTO) extends AnyVal {

    def toDO(lisMapping: Map[String, String]): Either[String, VoteMembersDTO] = {
      val unresolvedIds = dto.members
        .map(_.lisMemberId)
        .filterNot(lisMapping.contains)

      if (unresolvedIds.nonEmpty) {
        Left(s"Unresolved lisMemberIds: ${unresolvedIds.mkString(", ")}")
      } else {
        val results = dto.members.map { m =>
          VoteResultDTO(
            memberId = lisMapping.get(m.lisMemberId),
            firstName = Some(m.firstName),
            lastName = Some(m.lastName),
            voteCast = Some(m.voteCast),
            party = Some(m.party),
            state = Some(m.state),
          )
        }

        Right(
          VoteMembersDTO(
            congress = dto.congress,
            chamber = "Senate",
            rollCallNumber = dto.voteNumber,
            sessionNumber = Some(dto.session),
            startDate = Some(dto.voteDate),
            updateDate = None,
            result = Some(dto.result),
            voteType = None,
            legislationNumber = None,
            legislationType = None,
            legislationUrl = None,
            url = None,
            identifier = None,
            sourceDataUrl = None,
            voteQuestion = Some(dto.question),
            results = Some(results),
          )
        )
      }
    }

  }

}
