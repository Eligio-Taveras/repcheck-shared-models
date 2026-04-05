package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.bill.ChangeType

final case class SectionChange(
  sectionId: String,
  changeType: ChangeType,
  previousText: Option[String],
  currentText: Option[String],
  description: String,
)

object SectionChange {

  implicit val encoder: Encoder[SectionChange] = deriveEncoder[SectionChange]
  implicit val decoder: Decoder[SectionChange] = deriveDecoder[SectionChange]

}

final case class BillTextDiffOutput(
  previousVersionCode: String,
  currentVersionCode: String,
  billId: String,
  sections: List[SectionChange],
  summary: String,
  significanceScore: Double,
)

object BillTextDiffOutput {

  implicit val encoder: Encoder[BillTextDiffOutput] = deriveEncoder[BillTextDiffOutput]
  implicit val decoder: Decoder[BillTextDiffOutput] = deriveDecoder[BillTextDiffOutput]

}
