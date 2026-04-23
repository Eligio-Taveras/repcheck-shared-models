package repcheck.shared.models.congress.dto.conversions

import java.time.{Instant, LocalDate, OffsetDateTime, ZoneOffset, ZonedDateTime}

/**
 * Defensive parsers for the two date shapes the Congress.gov + senate.gov feeds surface:
 *
 *   - **Date-only** (`"2025-09-08"`) — used by policy/action dates.
 *   - **OffsetDateTime with timezone offset** (`"2025-09-09T18:53:19-04:00"`) — used by House-vote `startDate` /
 *     `updateDate`. `Instant.parse` rejects this format (it requires `Z` for UTC) and `LocalDate.parse` rejects any
 *     string with a time component. Both parsers here fall back through every shape the feed has been observed to
 *     produce before returning `None`.
 *   - **UTC Instant** (`"2025-09-09T22:53:19Z"`) — occasionally returned by some endpoints; supported for symmetry.
 *
 * Prior versions accepted only date-only for `toLocalDate` and only UTC-Instant (+ date-only) for `toInstant`, so real
 * Congress.gov responses silently produced `None` — votes never got an `updateDate` (change detection broken) and never
 * got a `voteDate` (archival to `vote_history.vote_date NOT NULL` crashed the first time a vote was updated).
 */
private[conversions] object DateParsing {

  /**
   * Parse a string into a `LocalDate`, trying each observed shape in turn:
   *   1. Pure `YYYY-MM-DD` via `LocalDate.parse` (fastest happy path for action dates). 2. Full `OffsetDateTime` —
   *      extract the date portion, dropping time + offset (Congress.gov House-vote `startDate`). 3. `ZonedDateTime` —
   *      same extraction, for zone-named variants.
   * Returns `None` on any unparseable input.
   */
  def toLocalDate(raw: Option[String]): Option[LocalDate] =
    raw.flatMap { s =>
      scala.util
        .Try(LocalDate.parse(s))
        .orElse(scala.util.Try(OffsetDateTime.parse(s).toLocalDate))
        .orElse(scala.util.Try(ZonedDateTime.parse(s).toLocalDate))
        .toOption
    }

  /**
   * Parse a string into an `Instant`, trying each observed shape in turn:
   *   1. `Instant.parse` — requires strict UTC (`Z` suffix). 2. `OffsetDateTime.parse(s).toInstant` — accepts `-04:00`
   *      / `+00:00` offsets (Congress.gov `updateDate`). 3. `ZonedDateTime.parse(s).toInstant` — accepts zone-id
   *      suffixes (e.g. `[America/New_York]`). 4. Date-only fallback — promotes `YYYY-MM-DD` to midnight UTC.
   * Returns `None` on any unparseable input.
   */
  def toInstant(raw: Option[String]): Option[Instant] =
    raw.flatMap { s =>
      scala.util
        .Try(Instant.parse(s))
        .orElse(scala.util.Try(OffsetDateTime.parse(s).toInstant))
        .orElse(scala.util.Try(ZonedDateTime.parse(s).toInstant))
        .orElse(scala.util.Try(LocalDate.parse(s).atStartOfDay(ZoneOffset.UTC).toInstant))
        .toOption
    }

}
