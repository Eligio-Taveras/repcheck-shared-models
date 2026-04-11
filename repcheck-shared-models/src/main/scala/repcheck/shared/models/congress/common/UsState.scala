package repcheck.shared.models.congress.common

import io.circe.{Decoder, Encoder}

final case class UnrecognizedUsState(value: String)
    extends Exception(
      s"Unrecognized US state: '$value'"
    )

/**
 * US states and territories as returned by the Congress.gov API. The API sends full state names (e.g., "Vermont",
 * "California") and occasionally 2-letter codes ("VT", "CA"). Both formats are supported by [[fromString]].
 */
enum UsState(val fullName: String, val code: String) {
  case Alabama              extends UsState("Alabama", "AL")
  case Alaska               extends UsState("Alaska", "AK")
  case Arizona              extends UsState("Arizona", "AZ")
  case Arkansas             extends UsState("Arkansas", "AR")
  case California           extends UsState("California", "CA")
  case Colorado             extends UsState("Colorado", "CO")
  case Connecticut          extends UsState("Connecticut", "CT")
  case Delaware             extends UsState("Delaware", "DE")
  case Florida              extends UsState("Florida", "FL")
  case Georgia              extends UsState("Georgia", "GA")
  case Hawaii               extends UsState("Hawaii", "HI")
  case Idaho                extends UsState("Idaho", "ID")
  case Illinois             extends UsState("Illinois", "IL")
  case Indiana              extends UsState("Indiana", "IN")
  case Iowa                 extends UsState("Iowa", "IA")
  case Kansas               extends UsState("Kansas", "KS")
  case Kentucky             extends UsState("Kentucky", "KY")
  case Louisiana            extends UsState("Louisiana", "LA")
  case Maine                extends UsState("Maine", "ME")
  case Maryland             extends UsState("Maryland", "MD")
  case Massachusetts        extends UsState("Massachusetts", "MA")
  case Michigan             extends UsState("Michigan", "MI")
  case Minnesota            extends UsState("Minnesota", "MN")
  case Mississippi          extends UsState("Mississippi", "MS")
  case Missouri             extends UsState("Missouri", "MO")
  case Montana              extends UsState("Montana", "MT")
  case Nebraska             extends UsState("Nebraska", "NE")
  case Nevada               extends UsState("Nevada", "NV")
  case NewHampshire         extends UsState("New Hampshire", "NH")
  case NewJersey            extends UsState("New Jersey", "NJ")
  case NewMexico            extends UsState("New Mexico", "NM")
  case NewYork              extends UsState("New York", "NY")
  case NorthCarolina        extends UsState("North Carolina", "NC")
  case NorthDakota          extends UsState("North Dakota", "ND")
  case Ohio                 extends UsState("Ohio", "OH")
  case Oklahoma             extends UsState("Oklahoma", "OK")
  case Oregon               extends UsState("Oregon", "OR")
  case Pennsylvania         extends UsState("Pennsylvania", "PA")
  case RhodeIsland          extends UsState("Rhode Island", "RI")
  case SouthCarolina        extends UsState("South Carolina", "SC")
  case SouthDakota          extends UsState("South Dakota", "SD")
  case Tennessee            extends UsState("Tennessee", "TN")
  case Texas                extends UsState("Texas", "TX")
  case Utah                 extends UsState("Utah", "UT")
  case Vermont              extends UsState("Vermont", "VT")
  case Virginia             extends UsState("Virginia", "VA")
  case Washington           extends UsState("Washington", "WA")
  case WestVirginia         extends UsState("West Virginia", "WV")
  case Wisconsin            extends UsState("Wisconsin", "WI")
  case Wyoming              extends UsState("Wyoming", "WY")
  case DistrictOfColumbia   extends UsState("District of Columbia", "DC")
  case PuertoRico           extends UsState("Puerto Rico", "PR")
  case Guam                 extends UsState("Guam", "GU")
  case AmericanSamoa        extends UsState("American Samoa", "AS")
  case USVirginIslands      extends UsState("Virgin Islands", "VI")
  case NorthernMarianaIsles extends UsState("Northern Mariana Islands", "MP")
}

object UsState {

  private val aliases: Map[String, UsState] = {
    val byName = UsState.values.map(s => s.fullName.toUpperCase -> s).toMap
    val byCode = UsState.values.map(s => s.code.toUpperCase -> s).toMap
    byName ++ byCode
  }

  def fromString(value: String): Either[UnrecognizedUsState, UsState] =
    aliases.get(value.trim.toUpperCase) match {
      case Some(s) => Right(s)
      case None    => Left(UnrecognizedUsState(value))
    }

  implicit val encoder: Encoder[UsState] =
    Encoder.encodeString.contramap(_.fullName)

  implicit val decoder: Decoder[UsState] = Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
