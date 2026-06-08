package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * A proposed taxonomy node: a concept name, its description, and (for hierarchy) its parent node's name, if any. Ids do
 * not exist at build time, so the parent is referenced by name.
 */
final case class ProposedNode(name: String, description: String, parentName: Option[String])

object ProposedNode {
  given Encoder[ProposedNode] = deriveEncoder[ProposedNode]
  given Decoder[ProposedNode] = deriveDecoder[ProposedNode]
}
