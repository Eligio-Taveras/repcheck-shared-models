package repcheck.shared.models.llm

import repcheck.shared.models.llm.codec.{LlmEnum, LlmEnumCompanion}

/**
 * The kind of wasteful/unrelated provision an LLM flags in pork detection (Component-10 analysis).
 *
 * Closed set via [[LlmEnumCompanion]]: schema `enum` (constrain) + decoder reject. See `LlmEnumRegistrySpec`.
 */
enum PorkType(val apiValue: String) extends LlmEnum {
  case Earmark            extends PorkType("earmark")
  case Rider              extends PorkType("rider")
  case UnrelatedProvision extends PorkType("unrelatedprovision")
}

object PorkType extends LlmEnumCompanion[PorkType] {
  protected def enumValues: Array[PorkType] = PorkType.values
  protected def label: String               = "PorkType"

  override protected def aliases: Map[String, PorkType] =
    Map("UNRELATED_PROVISION" -> PorkType.UnrelatedProvision, "UNRELATED-PROVISION" -> PorkType.UnrelatedProvision)

}
