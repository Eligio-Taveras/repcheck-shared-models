package repcheck.shared.models.llm

import io.circe.{Decoder, Encoder}

final case class UnrecognizedEffect(value: String)
    extends Exception(
      s"Unrecognized Effect: '$value'. Valid values: EXPANDS, RESTRICTS, CREATES, ELIMINATES, MODIFIES, REPORTS"
    )

/** The structural action a concept's topic takes on its area (vectors-primary stance schema). */
enum Effect(val apiValue: String) {
  case Expands    extends Effect("EXPANDS")
  case Restricts  extends Effect("RESTRICTS")
  case Creates    extends Effect("CREATES")
  case Eliminates extends Effect("ELIMINATES")
  case Modifies   extends Effect("MODIFIES")
  case Reports    extends Effect("REPORTS")
}

object Effect {

  private val lookup: Map[String, Effect] = {
    val byName = Effect.values.map(e => e.toString.toUpperCase -> e).toMap
    val byApi  = Effect.values.map(e => e.apiValue.toUpperCase -> e).toMap
    byName ++ byApi
  }

  def fromString(value: String): Either[UnrecognizedEffect, Effect] =
    lookup.get(value.toUpperCase) match {
      case Some(e) => Right(e)
      case None    => Left(UnrecognizedEffect(value))
    }

  implicit val encoder: Encoder[Effect] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[Effect] =
    Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
