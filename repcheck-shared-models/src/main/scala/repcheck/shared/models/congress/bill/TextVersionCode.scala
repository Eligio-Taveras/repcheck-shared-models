package repcheck.shared.models.congress.bill

import io.circe.{Decoder, Encoder}

final case class UnrecognizedTextVersionCode(value: String)
    extends Exception(
      s"Unrecognized TextVersionCode: '$value'. Valid values: IH, IS, RH, RS, RFS, RFH, EH, ES, ENR, CPH, CPS, PCS, PCH, PL, RDS, RDH, RTS, RTH, ATS, ATH, PP, RCH, EAS, RIS and their full names"
    )

enum TextVersionCode(val fullName: String) {
  case IH  extends TextVersionCode("Introduced in House")
  case IS  extends TextVersionCode("Introduced in Senate")
  case RH  extends TextVersionCode("Reported in House")
  case RS  extends TextVersionCode("Reported in Senate")
  case RFS extends TextVersionCode("Referred to Senate")
  case RFH extends TextVersionCode("Referred to House")
  case EH  extends TextVersionCode("Engrossed in House")
  case ES  extends TextVersionCode("Engrossed in Senate")
  case ENR extends TextVersionCode("Enrolled Bill")
  case CPH extends TextVersionCode("Committee Print (House)")
  case CPS extends TextVersionCode("Committee Print (Senate)")
  case PCS extends TextVersionCode("Placed on Calendar Senate")
  case PCH extends TextVersionCode("Placed on Calendar House")
  case PL  extends TextVersionCode("Public Law")
  case RDS extends TextVersionCode("Received in Senate")
  case RDH extends TextVersionCode("Received in House")
  case RTS extends TextVersionCode("Reported to Senate")
  case RTH extends TextVersionCode("Reported to House")
  case ATS extends TextVersionCode("Agreed to Senate")
  case ATH extends TextVersionCode("Agreed to House")
  case PP  extends TextVersionCode("Printed as Passed")
  case RCH extends TextVersionCode("Reference Change House")
  case EAS extends TextVersionCode("Engrossed Amendment Senate")
  case RIS extends TextVersionCode("Referral Instructions Senate")
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
