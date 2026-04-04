package repcheck.shared.models.llm

import io.circe.{Decoder, Encoder}

final case class UnrecognizedStanceType(value: String)
    extends Exception(
      s"Unrecognized StanceType: '$value'. Valid values: Conservative, Progressive, Bipartisan, Neutral"
    )

enum StanceType(val apiValue: String) {
  case Conservative extends StanceType("conservative")
  case Progressive  extends StanceType("progressive")
  case Bipartisan   extends StanceType("bipartisan")
  case Neutral      extends StanceType("neutral")
}

object StanceType {

  private val lookup: Map[String, StanceType] = {
    val byName = StanceType.values.map(st => st.toString.toUpperCase -> st).toMap
    val byApi  = StanceType.values.map(st => st.apiValue.toUpperCase -> st).toMap
    byName ++ byApi
  }

  def fromString(value: String): Either[UnrecognizedStanceType, StanceType] =
    lookup.get(value.toUpperCase) match {
      case Some(st) => Right(st)
      case None     => Left(UnrecognizedStanceType(value))
    }

  implicit val encoder: Encoder[StanceType] =
    Encoder.encodeString.contramap(_.apiValue)

  implicit val decoder: Decoder[StanceType] =
    Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
