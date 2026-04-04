package repcheck.shared.models.congress.dto.bill

import io.circe.{Decoder, Encoder, HCursor, Json}

final case class FormatDTO(
    type_ : String,
    url: String
)

object FormatDTO {
  implicit val encoder: Encoder[FormatDTO] = Encoder.instance { f =>
    Json.obj(
      "type" -> Json.fromString(f.type_),
      "url" -> Json.fromString(f.url)
    )
  }

  implicit val decoder: Decoder[FormatDTO] = (c: HCursor) => {
    for {
      t <- c.downField("type").as[String]
      url <- c.downField("url").as[String]
    } yield FormatDTO(type_ = t, url = url)
  }
}

final case class TextVersionDTO(
    date: Option[String],
    formats: Option[List[FormatDTO]],
    type_ : Option[String]
)

object TextVersionDTO {
  implicit val encoder: Encoder[TextVersionDTO] = Encoder.instance { tv =>
    val fields = List(
      tv.date.map(v => "date" -> Json.fromString(v)),
      tv.formats.map(v => "formats" -> Encoder.encodeList[FormatDTO].apply(v)),
      tv.type_.map(v => "type" -> Json.fromString(v))
    ).flatten
    Json.obj(fields*)
  }

  implicit val decoder: Decoder[TextVersionDTO] = (c: HCursor) => {
    for {
      date <- c.downField("date").as[Option[String]]
      formats <- c.downField("formats").as[Option[List[FormatDTO]]]
      t <- c.downField("type").as[Option[String]]
    } yield TextVersionDTO(date = date, formats = formats, type_ = t)
  }
}
