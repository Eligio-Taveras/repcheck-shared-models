package repcheck.shared.models.congress.dto.conversions

import repcheck.shared.models.congress.common.{Party, UsState}
import repcheck.shared.models.congress.dos.member.{MemberDO, MemberPartyHistoryDO, MemberTermDO}
import repcheck.shared.models.congress.dos.results.MemberConversionResult
import repcheck.shared.models.congress.dto.member.MemberDetailDTO

object MemberConversions {

  private def parseBirthYear(raw: Option[String]): Either[String, Option[Int]] =
    raw match {
      case None => Right(None)
      case Some(s) =>
        s.trim.toIntOption match {
          case Some(year) => Right(Some(year))
          case None       => Left(s"birthYear is not a valid integer: '$s'")
        }
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

  implicit class MemberDetailDTOOps(private val dto: MemberDetailDTO) extends AnyVal {

    def toDO: Either[String, MemberConversionResult] =
      if (dto.bioguideId.trim.isEmpty) {
        Left("bioguideId must not be empty")
      } else {
        val currentPartyRaw = dto.partyHistory
          .flatMap(_.lastOption)
          .flatMap(_.partyAbbreviation)

        val district = dto.terms
          .flatMap(_.lastOption)
          .flatMap(_.district)

        for {
          birthYear    <- parseBirthYear(dto.birthYear)
          currentParty <- parseParty(currentPartyRaw)
          state        <- parseState(dto.state)
        } yield {
          val member = MemberDO(
            memberId = 0L,
            naturalKey = dto.bioguideId,
            firstName = dto.firstName,
            lastName = dto.lastName,
            directOrderName = dto.directOrderName,
            invertedOrderName = dto.invertedOrderName,
            honorificName = dto.honorificName,
            birthYear = birthYear,
            currentParty = currentParty,
            state = state,
            district = district,
            imageUrl = dto.depiction.flatMap(_.imageUrl),
            imageAttribution = dto.depiction.flatMap(_.attribution),
            officialUrl = None,
            updateDate = dto.updateDate,
            createdAt = None,
            updatedAt = None,
          )

          val terms: List[MemberTermDO] = dto.terms
            .getOrElse(List.empty)
            .map { t =>
              MemberTermDO(
                termId = 0L,
                memberId = 0L,
                chamber = t.chamber,
                congress = t.congress,
                startYear = t.startYear,
                endYear = t.endYear,
                memberType = t.memberType,
                stateCode = t.stateCode,
                stateName = t.stateName,
                district = t.district,
              )
            }

          val partyHistory: List[MemberPartyHistoryDO] = dto.partyHistory
            .getOrElse(List.empty)
            .map { ph =>
              MemberPartyHistoryDO(
                id = 0L,
                memberId = 0L,
                partyName = ph.partyName,
                partyAbbreviation = ph.partyAbbreviation,
                startYear = ph.startYear,
              )
            }

          MemberConversionResult(
            member = member,
            terms = terms,
            partyHistory = partyHistory,
          )
        }
      }

  }

}
