package repcheck.shared.models.congress.dto.vote

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class SenateVoteMemberXmlDTO(
  lisMemberId: String,
  firstName: String,
  lastName: String,
  party: String,
  state: String,
  voteCast: String,
)

object SenateVoteMemberXmlDTO {
  implicit val encoder: Encoder[SenateVoteMemberXmlDTO] = deriveEncoder[SenateVoteMemberXmlDTO]
  implicit val decoder: Decoder[SenateVoteMemberXmlDTO] = deriveDecoder[SenateVoteMemberXmlDTO]
}

/**
 * Information about the legislative document the Senate vote is about. Always present on a `<roll_call_vote>` XML
 * document — even procedural votes on motions carry a `<document>` element identifying the underlying bill, resolution,
 * nomination, or treaty.
 *
 * `documentType` holds the raw senate.gov string (e.g. `"S."`, `"H.R."`, `"S.J.Res."`, `"PN"`, `"Treaty Doc."`), NOT
 * normalized to a [[repcheck.shared.models.congress.common.BillType]] enum value — senate.gov uses mixed formatting
 * with periods and varying case, and not every `documentType` maps to a `BillType` (nominations and treaties don't).
 * Consumers that need a `BillType` normalize the string at the call site; see the votes-pipeline's
 * `SenateVoteConverter` for the mapping.
 *
 * @param documentCongress
 *   Congress number the document belongs to. Usually matches the vote's own `congress`, but senate.gov carries it
 *   separately on the document.
 * @param documentType
 *   Raw senate.gov document-type string. For bill-like votes (`"S."`, `"H.R."`, `"S.J.Res."`, `"H.J.Res."`, `"S.Res."`,
 *   `"H.Res."`, `"S.Con.Res."`, `"H.Con.Res."`) the votes-pipeline normalizes to [[BillType.apiValue]] when building a
 *   bill natural key. For `"PN"` (Presidential Nomination) or `"Treaty Doc."`, there is no corresponding bill row and
 *   the vote is treated as bill-unlinked.
 * @param documentNumber
 *   The document identifier within its type. `"1071"` for `S. 1071`, `"11-11"` for nomination `PN11-11`, etc. Always a
 *   non-empty string.
 * @param documentName
 *   Short name of the document, e.g. `"S. 1071"` or `"PN11-11"`. Redundant with `documentType` + `documentNumber` but
 *   retained as it appears in the XML and simplifies log rendering.
 * @param documentTitle
 *   Human-readable title of the document. For bills, the bill's descriptive title ("A bill to require..."); for
 *   nominations, the nominee + position ("Kristi Noem, of South Dakota, to be Secretary of Homeland Security").
 * @param documentShortTitle
 *   Optional shorthand title used by senate.gov for well-known bills. Often empty — populated only when the document
 *   has a widely recognized short form.
 */
final case class SenateVoteDocumentDTO(
  documentCongress: Int,
  documentType: String,
  documentNumber: String,
  documentName: String,
  documentTitle: String,
  documentShortTitle: Option[String],
)

object SenateVoteDocumentDTO {
  implicit val encoder: Encoder[SenateVoteDocumentDTO] = deriveEncoder[SenateVoteDocumentDTO]
  implicit val decoder: Decoder[SenateVoteDocumentDTO] = deriveDecoder[SenateVoteDocumentDTO]
}

final case class SenateVoteXmlDTO(
  congress: Int,
  session: Int,
  voteNumber: Int,
  question: String,
  voteDate: String,
  result: String,
  document: SenateVoteDocumentDTO,
  members: List[SenateVoteMemberXmlDTO],
)

object SenateVoteXmlDTO {
  implicit val encoder: Encoder[SenateVoteXmlDTO] = deriveEncoder[SenateVoteXmlDTO]
  implicit val decoder: Decoder[SenateVoteXmlDTO] = deriveDecoder[SenateVoteXmlDTO]
}

final case class SenateMemberContactDTO(
  bioguideId: String,
  firstName: String,
  lastName: String,
  party: String,
  state: String,
  senateClass: Option[Int],
  address: Option[String],
  phone: Option[String],
  website: Option[String],
)

object SenateMemberContactDTO {
  implicit val encoder: Encoder[SenateMemberContactDTO] = deriveEncoder[SenateMemberContactDTO]
  implicit val decoder: Decoder[SenateMemberContactDTO] = deriveDecoder[SenateMemberContactDTO]
}

final case class ServicePeriodDTO(
  congress: Option[Int],
  startDate: Option[String],
  endDate: Option[String],
)

object ServicePeriodDTO {
  implicit val encoder: Encoder[ServicePeriodDTO] = deriveEncoder[ServicePeriodDTO]
  implicit val decoder: Decoder[ServicePeriodDTO] = deriveDecoder[ServicePeriodDTO]
}

final case class SenatorLookupXmlDTO(
  lisId: String,
  bioguideId: String,
  firstName: String,
  lastName: String,
  party: String,
  state: String,
  senateClass: Option[Int],
  serviceDates: List[ServicePeriodDTO],
  isCurrent: Boolean,
)

object SenatorLookupXmlDTO {
  implicit val encoder: Encoder[SenatorLookupXmlDTO] = deriveEncoder[SenatorLookupXmlDTO]
  implicit val decoder: Decoder[SenatorLookupXmlDTO] = deriveDecoder[SenatorLookupXmlDTO]
}
