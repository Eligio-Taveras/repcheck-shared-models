package repcheck.shared.models.llm

import io.circe.{Decoder, Encoder}

final case class UnrecognizedScope(value: String)
    extends Exception(
      s"Unrecognized Scope: '$value'. Valid values: MAJOR, MODERATE, MINOR"
    )

/**
 * How central a topic is to its concept: MAJOR central, MODERATE substantive but not central, MINOR a side-effect.
 *
 * Closed set enforced like [[Effect]]: the summarizer's `submit`-tool JSON schema publishes these `apiValue`s as an
 * `enum`, and the [[decoder]] rejects out-of-set values so the agentic enforcer re-prompts. See
 * `StanceSchemaEnforcementSpec`.
 */
enum Scope(val apiValue: String) {
  case Major    extends Scope("MAJOR")
  case Moderate extends Scope("MODERATE")
  case Minor    extends Scope("MINOR")
}

object Scope {

  private val lookup: Map[String, Scope] = {
    val byName = Scope.values.map(s => s.toString.toUpperCase -> s).toMap
    val byApi  = Scope.values.map(s => s.apiValue.toUpperCase -> s).toMap
    byName ++ byApi
  }

  def fromString(value: String): Either[UnrecognizedScope, Scope] =
    lookup.get(value.toUpperCase) match {
      case Some(s) => Right(s)
      case None    => Left(UnrecognizedScope(value))
    }

  implicit val encoder: Encoder[Scope] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[Scope] =
    Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
