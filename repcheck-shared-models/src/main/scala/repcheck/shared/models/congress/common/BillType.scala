package repcheck.shared.models.congress.common

import io.circe.{Decoder, Encoder}

final case class UnrecognizedBillType(value: String)
    extends Exception(
      s"Unrecognized BillType: '$value'. Valid values: HR, S, HJRES, SJRES, HCONRES, SCONRES, HRES, SRES, PL, STAT, USC, SRPT, HRPT"
    )

enum BillType(val apiValue: String) {
  case HR      extends BillType("hr")
  case S       extends BillType("s")
  case HJRES   extends BillType("hjres")
  case SJRES   extends BillType("sjres")
  case HCONRES extends BillType("hconres")
  case SCONRES extends BillType("sconres")
  case HRES    extends BillType("hres")
  case SRES    extends BillType("sres")
  case PL      extends BillType("pl")
  case STAT    extends BillType("stat")
  case USC     extends BillType("usc")
  case SRPT    extends BillType("srpt")
  case HRPT    extends BillType("hrpt")
}

object BillType {

  private val lookup: Map[String, BillType] = {
    val byName = BillType.values.map(bt => bt.toString.toUpperCase -> bt).toMap
    val byApi  = BillType.values.map(bt => bt.apiValue.toUpperCase -> bt).toMap
    byName ++ byApi
  }

  def fromString(value: String): Either[UnrecognizedBillType, BillType] =
    lookup.get(value.toUpperCase) match {
      case Some(bt) => Right(bt)
      case None     => Left(UnrecognizedBillType(value))
    }

  implicit val encoder: Encoder[BillType] =
    Encoder.encodeString.contramap(_.toString)

  implicit val decoder: Decoder[BillType] = Decoder.decodeString.emap(str => fromString(str).left.map(_.getMessage))

}
