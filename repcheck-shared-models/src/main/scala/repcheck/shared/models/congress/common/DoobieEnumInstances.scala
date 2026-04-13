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
 * Enums backed by PostgreSQL enum types (migration 013) use [[doobie.postgres.implicits.pgEnumStringOpt]] so that
 * Doobie sends the value with the correct `OTHER` JDBC type, avoiding `column X is of type Y but expression is of type
 * character varying` errors. Enums stored as plain `TEXT`/`VARCHAR` (e.g., `UsState`, `CommitteeSide`) use the standard
 * `Get[String]`/`Put[String]` approach.
 *
 * Import `DoobieEnumInstances._` wherever Doobie needs to read/write DOs with enum fields.
 */
object DoobieEnumInstances {

  // ---------------------------------------------------------------------------
  // PostgreSQL enum-backed types (migration 013-enum-type-constraints.sql)
  // ---------------------------------------------------------------------------

  private val partyMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "party_type",
    s => Party.fromString(s).toOption,
    _.apiValue,
  )

  implicit val partyGet: Get[Party] = partyMeta.get
  implicit val partyPut: Put[Party] = partyMeta.put

  private val chamberMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "chamber_type",
    s => Chamber.fromString(s).toOption,
    _.apiValue,
  )

  implicit val chamberGet: Get[Chamber] = chamberMeta.get
  implicit val chamberPut: Put[Chamber] = chamberMeta.put

  private val billTypeMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "bill_type_enum",
    s => BillType.fromString(s).toOption,
    _.apiValue,
  )

  implicit val billTypeGet: Get[BillType] = billTypeMeta.get
  implicit val billTypePut: Put[BillType] = billTypeMeta.put

  private val formatTypeMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "format_type_enum",
    s => FormatType.fromString(s).toOption,
    _.text,
  )

  implicit val formatTypeGet: Get[FormatType] = formatTypeMeta.get
  implicit val formatTypePut: Put[FormatType] = formatTypeMeta.put

  private val voteCastMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "vote_cast_type",
    s => VoteCast.fromString(s).toOption,
    _.apiValue,
  )

  implicit val voteCastGet: Get[VoteCast] = voteCastMeta.get
  implicit val voteCastPut: Put[VoteCast] = voteCastMeta.put

  private val amendmentTypeMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "amendment_type_enum",
    s => AmendmentType.fromString(s).toOption,
    _.apiValue,
  )

  implicit val amendmentTypeGet: Get[AmendmentType] = amendmentTypeMeta.get
  implicit val amendmentTypePut: Put[AmendmentType] = amendmentTypeMeta.put

  private val committeeTypeMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "committee_type_enum",
    s => CommitteeType.fromString(s).toOption,
    _.apiValue,
  )

  implicit val committeeTypeGet: Get[CommitteeType] = committeeTypeMeta.get
  implicit val committeeTypePut: Put[CommitteeType] = committeeTypeMeta.put

  private val committeePositionMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "committee_position_type",
    s => CommitteePosition.fromString(s).toOption,
    _.apiValue,
  )

  implicit val committeePositionGet: Get[CommitteePosition] = committeePositionMeta.get
  implicit val committeePositionPut: Put[CommitteePosition] = committeePositionMeta.put

  private val voteMethodMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "vote_method_type",
    s => VoteMethod.fromString(s).toOption,
    _.apiValue,
  )

  implicit val voteMethodGet: Get[VoteMethod] = voteMethodMeta.get
  implicit val voteMethodPut: Put[VoteMethod] = voteMethodMeta.put

  private val memberTypeMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "member_type_enum",
    s => MemberType.fromString(s).toOption,
    _.apiValue,
  )

  implicit val memberTypeGet: Get[MemberType] = memberTypeMeta.get
  implicit val memberTypePut: Put[MemberType] = memberTypeMeta.put

  private val textVersionCodeMeta = doobie.postgres.implicits.pgEnumStringOpt(
    "text_version_code_type",
    s => TextVersionCode.fromString(s).toOption,
    _.toString,
  )

  implicit val textVersionCodeGet: Get[TextVersionCode] = textVersionCodeMeta.get
  implicit val textVersionCodePut: Put[TextVersionCode] = textVersionCodeMeta.put

  // ---------------------------------------------------------------------------
  // Plain TEXT/VARCHAR-backed types (no PostgreSQL enum type)
  // ---------------------------------------------------------------------------

  implicit val usStateGet: Get[UsState] =
    Get[String].temap(s => UsState.fromString(s).left.map(_.getMessage))

  implicit val usStatePut: Put[UsState] =
    Put[String].contramap(_.code)

  implicit val committeeSideGet: Get[CommitteeSide] =
    Get[String].temap(s => CommitteeSide.fromString(s).left.map(_.getMessage))

  implicit val committeeSidePut: Put[CommitteeSide] =
    Put[String].contramap(_.apiValue)

}
