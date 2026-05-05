package repcheck.shared.models.congress.amendment

import io.circe.{Decoder, Encoder, Json}

import repcheck.shared.models.congress.common.BillType

/**
 * Tagged reference to either a bill or an amendment.
 *
 * Used by `SenateVoteConverter.normalizeDocumentType` (in repcheck-data-ingestion) to surface the legislation kind a
 * Senate vote document points at, replacing the prior `Either[NonBillOrUnknown, BillType]` shape which couldn't express
 * "this vote is on an amendment".
 */
sealed trait LegislationRef

object LegislationRef {

  final case class Bill(billType: BillType)                extends LegislationRef
  final case class Amendment(amendmentType: AmendmentType) extends LegislationRef

  // JSON shape: { "kind": "bill" | "amendment", "value": <enum apiValue> }
  // Discriminator-tagged — symmetric with how other sealed types are encoded across the repo.

  implicit val encoder: Encoder[LegislationRef] = Encoder.instance {
    case Bill(bt)      => Json.obj("kind" -> Json.fromString("bill"), "value" -> BillType.encoder(bt))
    case Amendment(at) => Json.obj("kind" -> Json.fromString("amendment"), "value" -> AmendmentType.encoder(at))
  }

  implicit val decoder: Decoder[LegislationRef] = Decoder.instance { c =>
    for {
      kind <- c.downField("kind").as[String]
      ref <- kind match {
        case "bill"      => c.downField("value").as[BillType].map(Bill.apply)
        case "amendment" => c.downField("value").as[AmendmentType].map(Amendment.apply)
        case other =>
          Left(
            io.circe
              .DecodingFailure(s"Unknown LegislationRef kind: '$other'. Expected 'bill' or 'amendment'.", c.history)
          )
      }
    } yield ref
  }

}
