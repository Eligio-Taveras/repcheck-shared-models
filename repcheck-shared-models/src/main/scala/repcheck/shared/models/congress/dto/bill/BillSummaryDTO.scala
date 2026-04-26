package repcheck.shared.models.congress.dto.bill

import io.circe.{Decoder, HCursor}

/**
 * Identifying reference to a bill carried inside each `/summaries` response entry. Lets the consumer look the bill up
 * by natural key without a full bill-detail fetch. Mirrors Congress.gov's `summaryBill` schema (`congress`, `type`,
 * `number` plus optional metadata we don't need here).
 */
final case class BillReferenceDTO(
  congress: Int,
  billType: String,
  number: Long,
) {

  /**
   * Build the natural key in the same format used by `BillConversions.buildBillNaturalKey` (e.g. `"118-HR-30"`). Note
   * the bill-type segment is uppercase to match the existing convention.
   */
  def naturalKey: String = s"$congress-${billType.toUpperCase}-${number.toString}"

}

object BillReferenceDTO {

  implicit val decoder: Decoder[BillReferenceDTO] = (c: HCursor) =>
    for {
      congress <- c.downField("congress").as[Int]
      billType <- c.downField("type").as[String]
      number   <- c.downField("number").as[Long]
    } yield BillReferenceDTO(congress = congress, billType = billType, number = number)

}

/**
 * One entry from the Congress.gov `/summaries` (or `/summaries/{congress}` / `/summaries/{congress}/{billType}`)
 * response. Each summary represents the bill's CRS-curated description at one legislative stage; the `versionCode`
 * field maps to a [[repcheck.shared.models.congress.bill.TextVersionCode]] via
 * [[repcheck.shared.models.congress.bill.SummaryVersionCodeMapper]].
 *
 * Bills accumulate summaries over time — one per major action — so a single bill can have many summary entries returned
 * across multiple paginated responses. The newest summary (highest `updateDate`) tells you the bill's current stage.
 *
 * @param actionDate
 *   when the corresponding legislative action happened (e.g. `"2022-02-18T16:38:41Z"`).
 * @param actionDesc
 *   CRS description of the action (e.g. `"Passed Senate"`, `"Introduced in House"`, `"Public Law"`). Provides a
 *   human-readable label; the `versionCode` is the structured signal.
 * @param text
 *   HTML body of the summary — typically a few hundred words written by CRS.
 * @param updateDate
 *   when this summary was last revised by CRS. Used as the ordering key when picking the "latest" summary for a bill.
 * @param versionCode
 *   CRS-internal numeric code for the legislative stage (e.g. `"00"`, `"36"`, `"49"`). The mapper's catalog is
 *   empirical — unknown codes raise [[repcheck.shared.models.congress.bill.UnrecognizedSummaryVersionCode]].
 * @param bill
 *   reference to the bill this summary describes. Always present in responses from the global `/summaries` family of
 *   endpoints; absent from the bill-scoped `/bill/{c}/{t}/{n}/summaries` endpoint (where the bill is implied by the URL
 *   path).
 */
final case class BillSummaryDTO(
  actionDate: Option[String],
  actionDesc: Option[String],
  text: Option[String],
  updateDate: String,
  versionCode: String,
  bill: Option[BillReferenceDTO],
)

object BillSummaryDTO {

  implicit val decoder: Decoder[BillSummaryDTO] = (c: HCursor) =>
    for {
      actionDate  <- c.downField("actionDate").as[Option[String]]
      actionDesc  <- c.downField("actionDesc").as[Option[String]]
      text        <- c.downField("text").as[Option[String]]
      updateDate  <- c.downField("updateDate").as[String]
      versionCode <- c.downField("versionCode").as[String]
      bill        <- c.downField("bill").as[Option[BillReferenceDTO]]
    } yield BillSummaryDTO(
      actionDate = actionDate,
      actionDesc = actionDesc,
      text = text,
      updateDate = updateDate,
      versionCode = versionCode,
      bill = bill,
    )

}
