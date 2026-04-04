package repcheck.shared.models.congress.common

import io.circe.{Decoder, Encoder}

final case class UnrecognizedFormatType(value: String)
    extends Exception(
      s"Unrecognized FormatType: '$value'. Valid values: FormattedText, PDF, FormattedXml"
    )

enum FormatType(val text: String) {
  case FormattedText extends FormatType("Formatted Text")
  case PDF           extends FormatType("PDF")
  case FormattedXml  extends FormatType("Formatted XML")
}

object FormatType {

  private val aliases: Map[String, FormatType] = Map(
    "FORMATTED TEXT" -> FormatType.FormattedText,
    "FORMATTEDTEXT"  -> FormatType.FormattedText,
    "PDF"            -> FormatType.PDF,
    "FORMATTED XML"  -> FormatType.FormattedXml,
    "FORMATTEDXML"   -> FormatType.FormattedXml,
  )

  def fromString(value: String): Either[UnrecognizedFormatType, FormatType] =
    aliases.get(value.toUpperCase) match {
      case Some(ft) => Right(ft)
      case None     => Left(UnrecognizedFormatType(value))
    }

  implicit val encoder: Encoder[FormatType] =
    Encoder.encodeString.contramap(_.text)

  implicit val decoder: Decoder[FormatType] = Decoder.decodeString.emap { str =>
    fromString(str).left.map(_.getMessage)
  }

}
