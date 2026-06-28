package repcheck.shared.models.llm

import repcheck.shared.models.llm.codec.{LlmEnum, LlmEnumCompanion}

/**
 * The political lean an LLM assigns to a topic in stance classification (Component-10 analysis).
 *
 * Closed set via [[LlmEnumCompanion]]: schema `enum` (constrain) + decoder reject. See `LlmEnumRegistrySpec`.
 */
enum StanceType(val apiValue: String) extends LlmEnum {
  case Conservative extends StanceType("conservative")
  case Progressive  extends StanceType("progressive")
  case Bipartisan   extends StanceType("bipartisan")
  case Neutral      extends StanceType("neutral")
}

object StanceType extends LlmEnumCompanion[StanceType] {
  protected def enumValues: Array[StanceType] = StanceType.values
  protected def label: String                 = "StanceType"
}
