package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import org.scalacheck.Gen
import repcheck.shared.models.llm.codec.StructuredCodec

/** LLM output of the open-set taxonomy build (D16): the proposed nodes curated from emergent concepts. */
final case class TaxonomyOutput(proposedNodes: List[ProposedNode])

object TaxonomyOutput {
  given Encoder[TaxonomyOutput] = deriveEncoder[TaxonomyOutput]
  given Decoder[TaxonomyOutput] = deriveDecoder[TaxonomyOutput]

  private val example = TaxonomyOutput(
    List(
      ProposedNode("Healthcare", "Laws affecting medical care, insurance, and public health.", None),
      ProposedNode("Medicare", "Provisions specific to the Medicare program.", Some("Healthcare")),
    )
  )

  private val gen: Gen[TaxonomyOutput] = {
    val node = for {
      name   <- Gen.identifier
      desc   <- Gen.alphaNumStr
      parent <- Gen.option(Gen.identifier)
    } yield ProposedNode(name, desc, parent)
    Gen.choose(0, 5).flatMap(n => Gen.listOfN(n, node)).map(TaxonomyOutput.apply)
  }

  given StructuredCodec[TaxonomyOutput] = StructuredCodec.instance(TapirSchemas.taxonomyOutput, example, gen)
}
