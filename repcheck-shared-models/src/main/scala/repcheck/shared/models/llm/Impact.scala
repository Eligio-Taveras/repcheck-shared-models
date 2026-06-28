package repcheck.shared.models.llm

import repcheck.shared.models.llm.codec.{LlmEnum, LlmEnumCompanion}

/**
 * Valence of a concept's topic against its neutrally-framed area: POSITIVE advances the area, NEGATIVE undermines it,
 * MIXED has real internal tradeoffs, NEUTRAL reorganizes without advancing or undermining.
 *
 * Closed set via [[LlmEnumCompanion]]: schema `enum` (constrain) + decoder reject. See `LlmEnumRegistrySpec`.
 */
enum Impact(val apiValue: String) extends LlmEnum {
  case Positive extends Impact("POSITIVE")
  case Negative extends Impact("NEGATIVE")
  case Mixed    extends Impact("MIXED")
  case Neutral  extends Impact("NEUTRAL")
}

object Impact extends LlmEnumCompanion[Impact] {
  protected def enumValues: Array[Impact] = Impact.values
  protected def label: String             = "Impact"
}
