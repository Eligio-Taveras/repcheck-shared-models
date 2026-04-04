package repcheck.shared.models.congress.bill

import io.circe.{Decoder, Encoder}

final case class UnrecognizedTextVersionCode(value: String)
    extends Exception(
      s"Unrecognized TextVersionCode: '$value'. Valid values: IH, IS, RH, RS, RFS, RFH, EH, ES, ENR, CPH, CPS and their full names"
    )

enum TextVersionCode(val fullName: String) {
  case IH  extends TextVersionCode("Introduced in House")
  case IS  extends TextVersionCode("Introduced in Senate")
  case RH  extends TextVersionCode("Reported in House")
  case RS  extends TextVersionCode("Reported in Senate")
  case RFS extends TextVersionCode("Referred in Senate")
  case RFH extends TextVersionCode("Referred in House")
  case EH  extends TextVersionCode("Engrossed in House")
  case ES  extends TextVersionCode("Engrossed in Senate")
  case ENR extends TextVersionCode("Enrolled Bill")
  case CPH extends TextVersionCode("Considered and Passed House")
  case CPS extends TextVersionCode("Considered and Passed Senate")
}

object TextVersionCode {

  private val aliases: Map[String, TextVersionCode] = {
    val byCode = TextVersionCode.values.map(tvc => tvc.toString.toUpperCase -> tvc).toMap
    val byName = TextVersionCode.values.map(tvc => tvc.fullName.toUpperCase -> tvc).toMap
    byCode ++ byName
  }

  def fromString(value: String): Either[UnrecognizedTextVersionCode, TextVersionCode] =
    aliases.get(value.toUpperCase) match {
      case Some(tvc) => Right(tvc)
      case None      => Left(UnrecognizedTextVersionCode(value))
    }

  implicit val encoder: Encoder[TextVersionCode] =
    Encoder.encodeString.contramap(_.toString)

  implicit val decoder: Decoder[TextVersionCode] = Decoder.decodeString.emap { str =>
    fromString(str).left.map(_.getMessage)
  }

}
