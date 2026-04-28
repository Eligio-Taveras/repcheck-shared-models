package repcheck.shared.models.congress.common

import io.circe.{Decoder, Encoder}

final case class UnrecognizedParty(value: String)
    extends Exception(
      s"Unrecognized Party: '$value'. Valid values: Democrat, Democratic, D, Republican, R, " +
        s"Independent, I, Independent Democrat, ID"
    )

enum Party(val apiValue: String) {
  case Democrat            extends Party("Democrat")
  case Republican          extends Party("Republican")
  case Independent         extends Party("Independent")
  case IndependentDemocrat extends Party("Independent Democrat")
}

object Party {

  // "ID" is the abbreviation for Independent-Democrat caucus (e.g., Joe Lieberman 2007–2013) —
  // a senator who registers as Independent but caucuses with the Democrats. Distinct from "I"
  // (true Independent, not caucusing with either party). The DB `party_type` enum was extended
  // in db-migrations changeset 033 to include the 'Independent Democrat' value, paired with
  // 'ID' in `party_abbreviation_type`. Mapping "ID" → IndependentDemocrat (not Independent)
  // preserves the political-affiliation nuance through the typed Scala layer.
  private val aliases: Map[String, Party] = Map(
    "DEMOCRAT"             -> Party.Democrat,
    "DEMOCRATIC"           -> Party.Democrat,
    "D"                    -> Party.Democrat,
    "REPUBLICAN"           -> Party.Republican,
    "R"                    -> Party.Republican,
    "INDEPENDENT"          -> Party.Independent,
    "I"                    -> Party.Independent,
    "INDEPENDENT DEMOCRAT" -> Party.IndependentDemocrat,
    "ID"                   -> Party.IndependentDemocrat,
  )

  def fromString(value: String): Either[UnrecognizedParty, Party] =
    aliases.get(value.toUpperCase) match {
      case Some(p) => Right(p)
      case None    => Left(UnrecognizedParty(value))
    }

  implicit val encoder: Encoder[Party] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[Party] = Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
