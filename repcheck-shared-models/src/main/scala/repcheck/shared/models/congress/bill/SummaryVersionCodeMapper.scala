package repcheck.shared.models.congress.bill

/**
 * Raised when [[SummaryVersionCodeMapper.toTextVersionCode]] is given a CRS summary `versionCode` that has no entry in
 * the mapper's catalog. Treated as a Systemic error by `bill-summary-pipeline`'s error classifier — the pipeline halts
 * on the first unrecognized code so the operator can add the new entry to the catalog and redeploy. This is the
 * fail-fast posture deliberately chosen so unmapped codes get immediate attention rather than silent gaps in
 * `bills.expected_text_version_code`.
 */
final case class UnrecognizedSummaryVersionCode(value: String)
    extends Exception(
      s"Unrecognized CRS summary versionCode: '$value'. Add the mapping to SummaryVersionCodeMapper.versionCodeToTextCode and redeploy. " +
        "The catalog is empirical — codes are added as they're observed in real /summaries responses."
    )

/**
 * Maps CRS summary `versionCode` values returned from Congress.gov's `/summaries` endpoint to our
 * domain [[TextVersionCode]] enum. The CRS-internal versionCode catalog is not officially documented;
 * the mappings below are seeded from observation of real `/summaries` responses and grow as new codes
 * are encountered in production. Unknown codes raise [[UnrecognizedSummaryVersionCode]] (Systemic) so
 * the pipeline halts immediately and the operator adds the new entry — see fail-fast posture decision
 * in the bill-summary-pipeline plan.
 *
 * Each entry's mapping target is the [[TextVersionCode]] that best represents the same legislative
 * stage. When in doubt, lean conservative: pick a code whose `progressionOrder` is at-or-below the
 * stage signaled by the summary's `actionDesc`. The regression guard at the write boundary will
 * still skip if our existing stored stage is more advanced.
 */
object SummaryVersionCodeMapper {

  /**
   * Empirical catalog of CRS summary versionCode → TextVersionCode mappings. Sourced from observation of Congress.gov
   * `/summaries` responses; not officially documented by CRS, so this catalog will grow as new codes are encountered in
   * the wild. Unknown codes raise [[UnrecognizedSummaryVersionCode]] (Systemic) — see scaladoc above.
   *
   * Initial seed covers the codes most commonly observed in `/summaries` responses. Add new entries here when
   * `bill-summary-pipeline` halts on an unknown code; include the `actionDesc` you observed alongside the new code in
   * a comment so the next operator knows what stage it represents.
   */
  private val versionCodeToTextCode: Map[String, TextVersionCode] = Map(
    "00" -> TextVersionCode.IH,  // "Introduced in House"
    "01" -> TextVersionCode.RTH, // "Reported to House"
    "13" -> TextVersionCode.RH,  // "Reported in House (Conference)" — final committee report
    "17" -> TextVersionCode.PL,  // "Public Law"
    "33" -> TextVersionCode.LTS, // "Laid on Table in Senate" — terminal
    "34" -> TextVersionCode.EAS, // "Passed Senate amended"
    "35" -> TextVersionCode.EH,  // "Passed House"
    "36" -> TextVersionCode.EAH, // "Passed House amended"
    "49" -> TextVersionCode.PL,  // "Became Public Law" — synonym for 17
    "55" -> TextVersionCode.EAS, // "Passed Senate as Amended"
    "70" -> TextVersionCode.RTS, // "Reported to Senate"
    "73" -> TextVersionCode.RS,  // "Reported in Senate"
  )

  def toTextVersionCode(versionCode: String): Either[UnrecognizedSummaryVersionCode, TextVersionCode] =
    versionCodeToTextCode.get(versionCode) match {
      case Some(tvc) => Right(tvc)
      case None      => Left(UnrecognizedSummaryVersionCode(versionCode))
    }

  /** All currently-mapped versionCodes. Useful for tests; the production lookup goes through [[toTextVersionCode]]. */
  def knownVersionCodes: Set[String] = versionCodeToTextCode.keySet

}
