package repcheck.shared.models.congress.dto.conversions

import java.util.UUID

import repcheck.shared.models.congress.dos.member.{MemberDO, MemberPartyHistoryDO, MemberTermDO}
import repcheck.shared.models.congress.dos.results.MemberConversionResult
import repcheck.shared.models.congress.dto.member.MemberDetailDTO

object MemberConversions {

  implicit class MemberDetailDTOOps(private val dto: MemberDetailDTO) extends AnyVal {
    def toDO: Either[String, MemberConversionResult] = toDO(() => UUID.randomUUID())

    def toDO(uuidGenerator: () => UUID): Either[String, MemberConversionResult] =
      if (dto.bioguideId.trim.isEmpty) {
        Left("bioguideId must not be empty")
      } else {
        val currentParty = dto.partyHistory
          .flatMap(_.lastOption)
          .flatMap(_.partyAbbreviation)

        val district = dto.terms
          .flatMap(_.lastOption)
          .flatMap(_.district)

        val member = MemberDO(
          memberId = 0L,
          naturalKey = dto.bioguideId,
          firstName = dto.firstName,
          lastName = dto.lastName,
          directOrderName = dto.directOrderName,
          invertedOrderName = dto.invertedOrderName,
          honorificName = dto.honorificName,
          birthYear = dto.birthYear,
          currentParty = currentParty,
          state = dto.state,
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
              termId = uuidGenerator(),
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
              partyHistoryId = uuidGenerator(),
              memberId = 0L,
              partyName = ph.partyName,
              partyAbbreviation = ph.partyAbbreviation,
              startYear = ph.startYear,
            )
          }

        Right(
          MemberConversionResult(
            member = member,
            terms = terms,
            partyHistory = partyHistory,
          )
        )
      }

  }

}
