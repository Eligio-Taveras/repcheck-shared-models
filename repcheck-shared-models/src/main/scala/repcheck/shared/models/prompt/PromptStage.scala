package repcheck.shared.models.prompt

import io.circe.{Decoder, Encoder}

final case class UnrecognizedPromptStage(value: String)
    extends Exception(
      s"Unrecognized PromptStage: '$value'. Valid values: System, Persona, Lens, Context, Custom, Guardrails, Output"
    )

enum PromptStage(val stageOrder: Int, val apiValue: String) {
  case System     extends PromptStage(0, "system")
  case Persona    extends PromptStage(1, "persona")
  case Lens       extends PromptStage(2, "lens")
  case Context    extends PromptStage(3, "context")
  case Custom     extends PromptStage(4, "custom")
  case Guardrails extends PromptStage(5, "guardrails")
  case Output     extends PromptStage(6, "output")
}

object PromptStage {

  private val lookup: Map[String, PromptStage] = {
    val byName = PromptStage.values.map(ps => ps.toString.toUpperCase -> ps).toMap
    val byApi  = PromptStage.values.map(ps => ps.apiValue.toUpperCase -> ps).toMap
    byName ++ byApi
  }

  def fromString(value: String): Either[UnrecognizedPromptStage, PromptStage] =
    lookup.get(value.toUpperCase) match {
      case Some(ps) => Right(ps)
      case None     => Left(UnrecognizedPromptStage(value))
    }

  implicit val encoder: Encoder[PromptStage] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[PromptStage] =
    Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
