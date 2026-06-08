package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import org.scalacheck.Gen
import repcheck.shared.models.llm.codec.StructuredCodec
import sttp.tapir.Schema

/**
 * One proposed taxonomy node from a taxonomy-build run: a concept name, its description, and (for hierarchy) the name
 * of its parent node, if any. Ids do not exist yet at build time — the parent is referenced by name.
 */
final case class ProposedNode(name: String, description: String, parentName: Option[String])

object ProposedNode {
  given Encoder[ProposedNode] = deriveEncoder[ProposedNode]
  given Decoder[ProposedNode] = deriveDecoder[ProposedNode]
  given Schema[ProposedNode]  = Schema.derived[ProposedNode]
}

/** LLM output of the open-set taxonomy build (D16): the set of proposed nodes curated from emergent concepts. */
final case class TaxonomyOutput(proposedNodes: List[ProposedNode])

object TaxonomyOutput {

  given StructuredCodec[TaxonomyOutput] = new StructuredCodec[TaxonomyOutput] {
    val encoder: Encoder[TaxonomyOutput]    = deriveEncoder[TaxonomyOutput]
    val decoder: Decoder[TaxonomyOutput]    = deriveDecoder[TaxonomyOutput]
    val tapirSchema: Schema[TaxonomyOutput] = Schema.derived[TaxonomyOutput]

    val canonicalExample: TaxonomyOutput = TaxonomyOutput(
      List(
        ProposedNode("Healthcare", "Laws affecting medical care, insurance, and public health.", None),
        ProposedNode("Medicare", "Provisions specific to the Medicare program.", Some("Healthcare")),
      )
    )

    val sampleGen: Gen[TaxonomyOutput] = {
      val node = for {
        name   <- Gen.identifier
        desc   <- Gen.alphaNumStr
        parent <- Gen.option(Gen.identifier)
      } yield ProposedNode(name, desc, parent)
      Gen.choose(0, 5).flatMap(n => Gen.listOfN(n, node)).map(TaxonomyOutput.apply)
    }
  }

}
