package repcheck.shared.models.congress.dto.conversions

import repcheck.shared.models.congress.common.{Chamber, Party}
import repcheck.shared.models.congress.dos.results.VoteConversionResult
import repcheck.shared.models.congress.dos.vote.{VoteDO, VotePositionDO}
import repcheck.shared.models.congress.dto.vote.{SenateVoteXmlDTO, VoteMembersDTO, VoteResultDTO}
import repcheck.shared.models.congress.vote.VoteCast

object VoteConversions {

  private[conversions] def buildVoteId(congress: Int, chamber: String, rollCallNumber: Int): String =
    s"$congress-$chamber-$rollCallNumber"

  private def parseChamber(raw: String): Either[String, Chamber] =
    Chamber.fromString(raw).left.map(_.getMessage)

  implicit class VoteMembersDTOOps(private val dto: VoteMembersDTO) extends AnyVal {

    def toDO: Either[String, VoteConversionResult] =
      if (dto.congress <= 0) {
        Left(s"congress must be > 0, got: ${dto.congress}")
      } else if (dto.chamber.trim.isEmpty) {
        Left("chamber must not be empty")
      } else {
        for {
          chamber <- parseChamber(dto.chamber)
        } yield {
          val naturalKey = buildVoteId(dto.congress, dto.chamber, dto.rollCallNumber)

          val vote = VoteDO(
            voteId = 0L,
            naturalKey = naturalKey,
            congress = dto.congress,
            chamber = chamber,
            rollNumber = dto.rollCallNumber,
            sessionNumber = dto.sessionNumber,
            billId = None,
            question = dto.voteQuestion,
            voteType = dto.voteType,
            voteMethod = None,
            result = dto.result,
            voteDate = dto.startDate,
            legislationNumber = dto.legislationNumber,
            legislationType = dto.legislationType,
            legislationUrl = dto.legislationUrl,
            sourceDataUrl = dto.sourceDataUrl.orElse(dto.url),
            updateDate = dto.updateDate,
            createdAt = None,
            updatedAt = None,
          )

          val positions: List[VotePositionDO] = dto.results
            .getOrElse(List.empty)
            .filter(_.memberId.isDefined)
            .map { r =>
              VotePositionDO(
                voteId = 0L,
                // Resolved to the AlloyDB-generated member PK in a downstream
                // step that joins on the Congress.gov member id (r.memberId).
                memberId = 0L,
                position = r.voteCast.flatMap(s => VoteCast.fromString(s).toOption),
                partyAtVote = r.party.flatMap(s => Party.fromString(s).toOption),
                stateAtVote = r.state,
                createdAt = None,
              )
            }

          VoteConversionResult(
            vote = vote,
            positions = positions,
          )
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
