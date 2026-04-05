package repcheck.shared.models.congress.dto.conversions

import java.time.Instant

import repcheck.shared.models.congress.dos.committee.{BillCommitteeReferralDO, CommitteeDO, CommitteeMemberDO}
import repcheck.shared.models.congress.dos.member.LisMemberMappingDO
import repcheck.shared.models.congress.dto.committee.{
  BillCommitteeReferralDTO,
  CommitteeListItemDTO,
  HouseMemberDataXmlDTO,
  SenatorCommitteeDataXmlDTO,
}

object CommitteeConversions {

  implicit class SenatorCommitteeDataXmlDTOOps(private val dto: SenatorCommitteeDataXmlDTO) extends AnyVal {

    def toMemberCommittees: List[CommitteeMemberDO] =
      dto.committees.map { assignment =>
        CommitteeMemberDO(
          committeeCode = assignment.committeeCode,
          memberId = dto.bioguideId,
          position = Some(assignment.position),
          side = None,
          rank = None,
          beginDate = None,
          endDate = None,
          congress = None,
          createdAt = None,
          updatedAt = None,
        )
      }

    def toLisMemberMapping: LisMemberMappingDO =
      LisMemberMappingDO(
        lisMemberId = dto.lisMemberId,
        memberId = dto.bioguideId,
        lastVerified = Instant.now(),
      )

  }

  implicit class HouseMemberDataXmlDTOOps(private val dto: HouseMemberDataXmlDTO) extends AnyVal {

    def toMemberCommittees: List[CommitteeMemberDO] =
      dto.committees.map { assignment =>
        CommitteeMemberDO(
          committeeCode = assignment.committeeCode,
          memberId = dto.bioguideId,
          position = None,
          side = Some(assignment.side),
          rank = assignment.rank,
          beginDate = None,
          endDate = None,
          congress = None,
          createdAt = None,
          updatedAt = None,
        )
      }

  }

  implicit class CommitteeListItemDTOOps(private val dto: CommitteeListItemDTO) extends AnyVal {

    def toDO: Either[String, CommitteeDO] =
      if (dto.systemCode.trim.isEmpty) {
        Left("systemCode must not be blank")
      } else {
        Right(
          CommitteeDO(
            committeeCode = dto.systemCode,
            name = dto.name,
            chamber = dto.chamber,
            committeeType = dto.committeeTypeCode,
            parentCommitteeCode = dto.parent.map(_.systemCode),
            isCurrent = None,
            updateDate = dto.updateDate,
            createdAt = None,
            updatedAt = None,
          )
        )
      }

  }

  implicit class BillCommitteeReferralDTOOps(private val dto: BillCommitteeReferralDTO) extends AnyVal {

    def toDO(billId: String): Either[String, BillCommitteeReferralDO] =
      if (dto.committeeCode.trim.isEmpty) {
        Left("committeeCode must not be blank")
      } else {
        val referralDate = dto.activities
          .filter(_.name.startsWith("Referred to"))
          .flatMap(_.date)
          .sorted
          .headOption

        val reportDate = dto.activities
          .filter(_.name.startsWith("Reported by"))
          .flatMap(_.date)
          .sorted
          .headOption

        val activityNames = dto.activities.map(_.name)
        val activity = if (activityNames.nonEmpty) { Some(activityNames.mkString("; ")) }
        else { None }

        Right(
          BillCommitteeReferralDO(
            billId = billId,
            committeeCode = dto.committeeCode,
            referralDate = referralDate,
            reportDate = reportDate,
            activity = activity,
            createdAt = None,
          )
        )
      }

  }

}
