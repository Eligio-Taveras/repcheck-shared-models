package repcheck.shared.models.congress.common

import doobie.{Get, Put}

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

}
