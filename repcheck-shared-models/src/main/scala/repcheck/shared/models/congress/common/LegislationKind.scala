package repcheck.shared.models.congress.common

import io.circe.{Decoder, Encoder}

final case class UnrecognizedLegislationKind(value: String)
    extends Exception(s"Unrecognized LegislationKind: '$value'. Valid values: BILL, AMENDMENT")

/**
 * Discriminator for the kind of legislation a vote (or other entity) refers to. Storage uses this enum on its own
 * (`legislation_type` column) alongside two narrower enum columns (`bill_type`, `amendment_type`) — exactly one of
 * those narrow columns is populated per row, matching this discriminator. See `VoteDO` for the field-level invariants.
 *
 * Distinct from [[repcheck.shared.models.congress.amendment.LegislationRef]]: that's a tagged-union DTO carrying the
 * narrow subtype inline; this enum is the storage-side discriminator. Both can coexist — they serve different layers.
 */
enum LegislationKind(val apiValue: String) {
  case BILL      extends LegislationKind("BILL")
  case AMENDMENT extends LegislationKind("AMENDMENT")
}

object LegislationKind {

  private val lookup: Map[String, LegislationKind] =
    LegislationKind.values.map(k => k.apiValue.toUpperCase -> k).toMap

  def fromString(value: String): Either[UnrecognizedLegislationKind, LegislationKind] =
    lookup.get(value.toUpperCase) match {
      case Some(k) => Right(k)
      case None    => Left(UnrecognizedLegislationKind(value))
    }

  implicit val encoder: Encoder[LegislationKind] = Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[LegislationKind] =
    Decoder.decodeString.emap(s => fromString(s).left.map(_.getMessage))

}
