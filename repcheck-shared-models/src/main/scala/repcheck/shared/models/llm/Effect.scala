package repcheck.shared.models.llm

import repcheck.shared.models.llm.codec.{LlmEnum, LlmEnumCompanion}

/**
 * The structural action a concept's topic takes on its area (vectors-primary stance schema).
 *
 * Closed set via [[LlmEnumCompanion]]: the `submit`-tool JSON schema publishes these `apiValue`s as an `enum`
 * (constrain) and the circe decoder rejects out-of-set values (reject). See `LlmEnumRegistrySpec`.
 */
enum Effect(val apiValue: String) extends LlmEnum {
  case Expands    extends Effect("EXPANDS")
  case Restricts  extends Effect("RESTRICTS")
  case Creates    extends Effect("CREATES")
  case Eliminates extends Effect("ELIMINATES")
  case Modifies   extends Effect("MODIFIES")
  case Reports    extends Effect("REPORTS")
}

object Effect extends LlmEnumCompanion[Effect] {
  protected def enumValues: Array[Effect] = Effect.values
  protected def label: String             = "Effect"
}
