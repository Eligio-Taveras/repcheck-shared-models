package repcheck.shared.models.llm

import io.circe.{Decoder, Encoder}

final case class UnrecognizedImpact(value: String)
    extends Exception(
      s"Unrecognized Impact: '$value'. Valid values: POSITIVE, NEGATIVE, MIXED, NEUTRAL"
    )

/**
 * Valence of a concept's topic against its neutrally-framed area: POSITIVE advances the area, NEGATIVE undermines it,
 * MIXED has real internal tradeoffs, NEUTRAL reorganizes without advancing or undermining.
 *
 * Closed set enforced like [[Effect]]: the summarizer's `submit`-tool JSON schema publishes these `apiValue`s as an
 * `enum`, and the [[decoder]] rejects out-of-set values so the agentic enforcer re-prompts. See
 * `StanceSchemaEnforcementSpec`.
 */
enum Impact(val apiValue: String) {
  case Positive extends Impact("POSITIVE")
  case Negative extends Impact("NEGATIVE")
  case Mixed    extends Impact("MIXED")
  case Neutral  extends Impact("NEUTRAL")
}

object Impact {

  private val lookup: Map[String, Impact] = {
    val byName = Impact.values.map(i => i.toString.toUpperCase -> i).toMap
    val byApi  = Impact.values.map(i => i.apiValue.toUpperCase -> i).toMap
    byName ++ byApi
  }

  def fromString(value: String): Either[UnrecognizedImpact, Impact] =
    lookup.get(value.toUpperCase) match {
      case Some(i) => Right(i)
      case None    => Left(UnrecognizedImpact(value))
    }

  implicit val encoder: Encoder[Impact] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[Impact] =
    Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
