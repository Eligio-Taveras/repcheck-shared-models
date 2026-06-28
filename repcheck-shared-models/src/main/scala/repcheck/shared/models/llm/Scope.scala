package repcheck.shared.models.llm

import repcheck.shared.models.llm.codec.{LlmEnum, LlmEnumCompanion}

/**
 * How central a topic is to its concept: MAJOR central, MODERATE substantive but not central, MINOR a side-effect.
 *
 * Closed set via [[LlmEnumCompanion]]: schema `enum` (constrain) + decoder reject. See `LlmEnumRegistrySpec`.
 */
enum Scope(val apiValue: String) extends LlmEnum {
  case Major    extends Scope("MAJOR")
  case Moderate extends Scope("MODERATE")
  case Minor    extends Scope("MINOR")
}

object Scope extends LlmEnumCompanion[Scope] {
  protected def enumValues: Array[Scope] = Scope.values
  protected def label: String            = "Scope"
}
