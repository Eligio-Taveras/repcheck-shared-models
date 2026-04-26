package repcheck.shared.models.congress.bill

import io.circe.{Decoder, Encoder}

final case class UnrecognizedTextVersionCode(value: String)
    extends Exception(
      s"Unrecognized TextVersionCode: '$value'. Valid values: IH, IS, RH, RS, RFS, RFH, EH, ES, ENR, CPH, CPS, PCS, PCH, PL, PRL, RDS, RDH, RTS, RTH, ATS, ATH, PP, RCH, RCS, EAS, EAH, RIS, RIH, LTH, LTS and their full names"
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
  case EAS extends TextVersionCode("Engrossed Amendment Senate")
  case EAH extends TextVersionCode("Engrossed Amendment House")
  case ENR extends TextVersionCode("Enrolled Bill")
  case CPH extends TextVersionCode("Committee Print (House)")
  case CPS extends TextVersionCode("Committee Print (Senate)")
  case PCS extends TextVersionCode("Placed on Calendar Senate")
  case PCH extends TextVersionCode("Placed on Calendar House")
  case PL  extends TextVersionCode("Public Law")
  case PRL extends TextVersionCode("Private Law")
  case RDS extends TextVersionCode("Received in Senate")
  case RDH extends TextVersionCode("Received in House")
  case RTS extends TextVersionCode("Reported to Senate")
  case RTH extends TextVersionCode("Reported to House")
  case ATS extends TextVersionCode("Agreed to Senate")
  case ATH extends TextVersionCode("Agreed to House")
  case PP  extends TextVersionCode("Printed as Passed")
  case RCH extends TextVersionCode("Reference Change House")
  case RCS extends TextVersionCode("Reference Change Senate")
  case RIS extends TextVersionCode("Referral Instructions Senate")
  case RIH extends TextVersionCode("Referral Instructions House")
  case LTH extends TextVersionCode("Laid on Table in House")
  case LTS extends TextVersionCode("Laid on Table in Senate")
}

object TextVersionCode {

  /**
   * Extra aliases for cases where Congress.gov emits more than one descriptive name for the same canonical code. The
   * primary name lives on the enum case's `fullName`; this map covers the secondary names so [[fromString]] resolves
   * cleanly without throwing `UnrecognizedTextVersionCode`.
   */
  private val extraAliases: Map[String, TextVersionCode] = Map(
    "PUBLIC PRINT" -> TextVersionCode.PP // canonical fullName is "Printed as Passed"; the API also emits "Public Print"
  )

  private val aliases: Map[String, TextVersionCode] = {
    val byCode = TextVersionCode.values.map(tvc => tvc.toString.toUpperCase -> tvc).toMap
    val byName = TextVersionCode.values.map(tvc => tvc.fullName.toUpperCase -> tvc).toMap
    byCode ++ byName ++ extraAliases
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
