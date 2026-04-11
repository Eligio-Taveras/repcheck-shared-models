package repcheck.shared.models.congress.dto.conversions

import java.time.{Instant, LocalDate, ZoneOffset}

private[conversions] object DateParsing {

  def toLocalDate(raw: Option[String]): Option[LocalDate] =
    raw.flatMap(s => scala.util.Try(LocalDate.parse(s)).toOption)

  def toInstant(raw: Option[String]): Option[Instant] =
    raw.flatMap { s =>
      scala.util
        .Try(Instant.parse(s))
        .orElse(scala.util.Try(LocalDate.parse(s).atStartOfDay(ZoneOffset.UTC).toInstant))
        .toOption
    }

}
