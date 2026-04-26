package repcheck.shared.models.congress.bill

import io.circe.{Decoder, Encoder}

final case class UnrecognizedTextVersionCode(value: String)
    extends Exception(
      s"Unrecognized TextVersionCode: '$value'. Valid values: IH, IS, RH, RS, RFS, RFH, EH, ES, ENR, CPH, CPS, PCS, PCH, PL, PRL, RDS, RDH, RTS, RTH, ATS, ATH, PP, RCH, RCS, EAS, EAH, RIS, RIH, LTH, LTS and their full names"
    )

/**
 * `progressionOrder` ranks each text-version code by how far the bill has progressed through the legislative pipeline.
 * Used by the cooperative-write contract on `bills.expected_text_version_code` (set by both bill-metadata-pipeline and
 * bill-summary-pipeline) — neither writer may downgrade an already-advanced bill: an UPDATE only fires when
 * `newCode.progressionOrder > existing.progressionOrder`.
 *
 * The grouping is intentionally coarse (8 tiers) — the exact relative order between codes within a tier doesn't matter
 * for the regression guard, only that the tiers are monotonic with bill progression. Codes not yet observed in real
 * Congress.gov responses are placed by analogy; iterate as we learn.
 *
 * Tiers:
 *   - 10 — Introduced (IH, IS)
 *   - 20 — Pre-committee referral / cross-chamber received (RFS, RFH, RIS, RIH, RCH, RCS, RDH, RDS)
 *   - 30 — Reported by committee / committee print (RH, RS, RTH, RTS, CPH, CPS)
 *   - 40 — Placed on calendar (PCH, PCS)
 *   - 50 — Engrossed in originating chamber (EH, ES)
 *   - 60 — Engrossed amendment (cross-chamber) / agreed-to / printed-as-passed (EAH, EAS, ATH, ATS, PP)
 *   - 70 — Enrolled (ENR)
 *   - 80 — Law (PL, PRL)
 *   - 99 — Tabled (LTH, LTS) — terminal "bill killed" state; treated as max so a stale earlier-stage summary can't
 *     overwrite a tabled bill back into the sweep.
 */
enum TextVersionCode(val fullName: String, val progressionOrder: Int) {
  case IH  extends TextVersionCode("Introduced in House", 10)
  case IS  extends TextVersionCode("Introduced in Senate", 10)
  case RFS extends TextVersionCode("Referred to Senate", 20)
  case RFH extends TextVersionCode("Referred to House", 20)
  case RIS extends TextVersionCode("Referral Instructions Senate", 20)
  case RIH extends TextVersionCode("Referral Instructions House", 20)
  case RCH extends TextVersionCode("Reference Change House", 20)
  case RCS extends TextVersionCode("Reference Change Senate", 20)
  case RDH extends TextVersionCode("Received in House", 20)
  case RDS extends TextVersionCode("Received in Senate", 20)
  case RH  extends TextVersionCode("Reported in House", 30)
  case RS  extends TextVersionCode("Reported in Senate", 30)
  case RTH extends TextVersionCode("Reported to House", 30)
  case RTS extends TextVersionCode("Reported to Senate", 30)
  case CPH extends TextVersionCode("Committee Print (House)", 30)
  case CPS extends TextVersionCode("Committee Print (Senate)", 30)
  case PCH extends TextVersionCode("Placed on Calendar House", 40)
  case PCS extends TextVersionCode("Placed on Calendar Senate", 40)
  case EH  extends TextVersionCode("Engrossed in House", 50)
  case ES  extends TextVersionCode("Engrossed in Senate", 50)
  case EAH extends TextVersionCode("Engrossed Amendment House", 60)
  case EAS extends TextVersionCode("Engrossed Amendment Senate", 60)
  case ATH extends TextVersionCode("Agreed to House", 60)
  case ATS extends TextVersionCode("Agreed to Senate", 60)
  case PP  extends TextVersionCode("Printed as Passed", 60)
  case ENR extends TextVersionCode("Enrolled Bill", 70)
  case PL  extends TextVersionCode("Public Law", 80)
  case PRL extends TextVersionCode("Private Law", 80)
  case LTH extends TextVersionCode("Laid on Table in House", 99)
  case LTS extends TextVersionCode("Laid on Table in Senate", 99)
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
