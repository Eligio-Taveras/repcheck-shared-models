package repcheck.shared.models.congress.common

import doobie.{Get, Put}

import repcheck.shared.models.congress.amendment.AmendmentType
import repcheck.shared.models.congress.bill.TextVersionCode
import repcheck.shared.models.congress.committee.{CommitteePosition, CommitteeSide, CommitteeType}
import repcheck.shared.models.congress.member.MemberType
import repcheck.shared.models.congress.vote.{VoteCast, VoteMethod}

/**
 * Doobie [[Get]] and [[Put]] instances for all domain enums. These enable Doobie auto-derivation of
 * [[doobie.Read]]/[[doobie.Write]] for DOs that contain enum fields (e.g., `MemberDO.currentParty: Option[Party]`).
 *
 * Import `DoobieEnumInstances._` wherever Doobie needs to read/write DOs with enum fields.
 */
object DoobieEnumInstances {

  implicit val partyGet: Get[Party] =
    Get[String].temap(s => Party.fromString(s).left.map(_.getMessage))

  implicit val partyPut: Put[Party] =
    Put[String].contramap(_.apiValue)

  implicit val chamberGet: Get[Chamber] =
    Get[String].temap(s => Chamber.fromString(s).left.map(_.getMessage))

  implicit val chamberPut: Put[Chamber] =
    Put[String].contramap(_.apiValue)

  implicit val usStateGet: Get[UsState] =
    Get[String].temap(s => UsState.fromString(s).left.map(_.getMessage))

  implicit val usStatePut: Put[UsState] =
    Put[String].contramap(_.fullName)

  implicit val billTypeGet: Get[BillType] =
    Get[String].temap(s => BillType.fromString(s).left.map(_.getMessage))

  implicit val billTypePut: Put[BillType] =
    Put[String].contramap(_.apiValue)

  implicit val formatTypeGet: Get[FormatType] =
    Get[String].temap(s => FormatType.fromString(s).left.map(_.getMessage))

  implicit val formatTypePut: Put[FormatType] =
    Put[String].contramap(_.text)

  implicit val voteCastGet: Get[VoteCast] =
    Get[String].temap(s => VoteCast.fromString(s).left.map(_.getMessage))

  implicit val voteCastPut: Put[VoteCast] =
    Put[String].contramap(_.apiValue)

  implicit val amendmentTypeGet: Get[AmendmentType] =
    Get[String].temap(s => AmendmentType.fromString(s).left.map(_.getMessage))

  implicit val amendmentTypePut: Put[AmendmentType] =
    Put[String].contramap(_.apiValue)

  implicit val committeeTypeGet: Get[CommitteeType] =
    Get[String].temap(s => CommitteeType.fromString(s).left.map(_.getMessage))

  implicit val committeeTypePut: Put[CommitteeType] =
    Put[String].contramap(_.apiValue)

  implicit val committeePositionGet: Get[CommitteePosition] =
    Get[String].temap(s => CommitteePosition.fromString(s).left.map(_.getMessage))

  implicit val committeePositionPut: Put[CommitteePosition] =
    Put[String].contramap(_.apiValue)

  implicit val committeeSideGet: Get[CommitteeSide] =
    Get[String].temap(s => CommitteeSide.fromString(s).left.map(_.getMessage))

  implicit val committeeSidePut: Put[CommitteeSide] =
    Put[String].contramap(_.apiValue)

  implicit val voteMethodGet: Get[VoteMethod] =
    Get[String].temap(s => VoteMethod.fromString(s).left.map(_.getMessage))

  implicit val voteMethodPut: Put[VoteMethod] =
    Put[String].contramap(_.apiValue)

  implicit val memberTypeGet: Get[MemberType] =
    Get[String].temap(s => MemberType.fromString(s).left.map(_.getMessage))

  implicit val memberTypePut: Put[MemberType] =
    Put[String].contramap(_.apiValue)

  implicit val textVersionCodeGet: Get[TextVersionCode] =
    Get[String].temap(s => TextVersionCode.fromString(s).left.map(_.getMessage))

  implicit val textVersionCodePut: Put[TextVersionCode] =
    Put[String].contramap(_.toString)

}
