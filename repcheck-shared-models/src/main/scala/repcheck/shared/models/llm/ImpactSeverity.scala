package repcheck.shared.models.llm

import repcheck.shared.models.llm.codec.{LlmEnum, LlmEnumCompanion}

/**
 * Severity an LLM assigns to an impact or pork finding (Component-10 analysis).
 *
 * Closed set via [[LlmEnumCompanion]]: schema `enum` (constrain) + decoder reject. See `LlmEnumRegistrySpec`.
 */
enum ImpactSeverity(val apiValue: String) extends LlmEnum {
  case High   extends ImpactSeverity("high")
  case Medium extends ImpactSeverity("medium")
  case Low    extends ImpactSeverity("low")
}

object ImpactSeverity extends LlmEnumCompanion[ImpactSeverity] {
  protected def enumValues: Array[ImpactSeverity] = ImpactSeverity.values
  protected def label: String                     = "ImpactSeverity"
}
