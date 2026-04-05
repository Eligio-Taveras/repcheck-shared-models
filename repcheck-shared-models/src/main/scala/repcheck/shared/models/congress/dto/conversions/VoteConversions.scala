package repcheck.shared.models.congress.dto.conversions

import repcheck.shared.models.congress.dos.results.VoteConversionResult
import repcheck.shared.models.congress.dos.vote.{VoteDO, VotePositionDO}
import repcheck.shared.models.congress.dto.vote.{SenateVoteXmlDTO, VoteMembersDTO, VoteResultDTO}

object VoteConversions {

  private[conversions] def buildVoteId(congress: Int, chamber: String, rollCallNumber: Int): String =
    s"$congress-$chamber-$rollCallNumber"

  implicit class VoteMembersDTOOps(private val dto: VoteMembersDTO) extends AnyVal {

    def toDO: Either[String, VoteConversionResult] =
      if (dto.congress <= 0) {
        Left(s"congress must be > 0, got: ${dto.congress}")
      } else if (dto.chamber.trim.isEmpty) {
        Left("chamber must not be empty")
      } else {
        val voteId = buildVoteId(dto.congress, dto.chamber, dto.rollCallNumber)

        val vote = VoteDO(
          voteId = voteId,
          congress = dto.congress,
          chamber = dto.chamber,
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
          sourceDataUrl = dto.url,
          updateDate = dto.updateDate,
          createdAt = None,
          updatedAt = None,
        )

        val positions: List[VotePositionDO] = dto.results
          .getOrElse(List.empty)
          .flatMap { r =>
            r.memberId.map { mid =>
              VotePositionDO(
                voteId = voteId,
                memberId = mid,
                position = r.voteCast,
                partyAtVote = r.party,
                stateAtVote = r.state,
                createdAt = None,
              )
            }
          }

        Right(
          VoteConversionResult(
            vote = vote,
            positions = positions,
          )
        )
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
            voteQuestion = Some(dto.question),
            results = Some(results),
          )
        )
      }
    }

  }

}
