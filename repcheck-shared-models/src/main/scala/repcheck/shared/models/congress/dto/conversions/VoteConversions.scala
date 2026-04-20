package repcheck.shared.models.congress.dto.conversions

import cats.syntax.traverse._

import repcheck.shared.models.congress.common.{BillType, Chamber, Party, UsState}
import repcheck.shared.models.congress.dos.results.VoteConversionResult
import repcheck.shared.models.congress.dos.vote.{VoteDO, VotePositionDO}
import repcheck.shared.models.congress.dto.vote.{SenateVoteXmlDTO, VoteMembersDTO, VoteResultDTO}
import repcheck.shared.models.congress.vote.{VoteCast, VoteType}

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

  implicit class VoteMembersDTOOps(private val dto: VoteMembersDTO) extends AnyVal {

    def toDO: Either[String, VoteConversionResult] =
      if (dto.congress <= 0) {
        Left(s"congress must be > 0, got: ${dto.congress}")
      } else if (dto.chamber.trim.isEmpty) {
        Left("chamber must not be empty")
      } else {
        dto.sessionNumber match {
          case None =>
            Left("sessionNumber is required for vote natural-key construction")
          case Some(session) =>
            for {
              chamber         <- parseChamber(dto.chamber)
              legislationType <- parseLegislationType(dto.legislationType)
              positions <- dto.results
                .getOrElse(List.empty)
                .filter(_.memberId.isDefined)
                .traverse { r =>
                  for {
                    position    <- parseVoteCast(r.voteCast)
                    partyAtVote <- parseParty(r.party)
                    stateAtVote <- parseState(r.state)
                  } yield VotePositionDO(
                    voteId = 0L,
                    // Resolved to the AlloyDB-generated member PK in a downstream
                    // step that joins on the Congress.gov member id (r.memberId).
                    memberId = 0L,
                    position = position,
                    partyAtVote = partyAtVote,
                    stateAtVote = stateAtVote,
                    createdAt = None,
                  )
                }
            } yield {
              val naturalKey     = buildVoteNaturalKey(dto.congress, dto.chamber, session, dto.rollCallNumber)
              val classifiedType = dto.voteQuestion.map(VoteType.fromQuestion)

              val vote = VoteDO(
                voteId = 0L,
                naturalKey = naturalKey,
                congress = dto.congress,
                chamber = chamber,
                rollNumber = dto.rollCallNumber,
                sessionNumber = dto.sessionNumber,
                billId = None,
                question = dto.voteQuestion,
                voteType = classifiedType,
                voteMethod = None,
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

              VoteConversionResult(
                vote = vote,
                positions = positions,
              )
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
